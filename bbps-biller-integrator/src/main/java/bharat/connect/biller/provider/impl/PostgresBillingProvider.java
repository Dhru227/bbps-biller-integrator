package bharat.connect.biller.provider.impl;

import bharat.connect.biller.model.BillDetails;
import bharat.connect.biller.provider.BillingProvider;
import bharat.connect.biller.provider.BillingProviderProperties;
import bharat.connect.biller.provider.CustomerParamCriterion;
import bharat.connect.biller.provider.PaymentUpdateRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class PostgresBillingProvider implements BillingProvider {
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z_][A-Za-z0-9_\\.]*$");

    private final NamedParameterJdbcTemplate jdbcNamedTemplate;
    private final BillingProviderProperties.Postgres postgres;

    public PostgresBillingProvider(@Qualifier("jdbcNamedTemplate") NamedParameterJdbcTemplate jdbcNamedTemplate,
                                   BillingProviderProperties properties) {
        this.jdbcNamedTemplate = jdbcNamedTemplate;
        this.postgres = properties.getPostgres();
    }

    @Override
    public BillDetails findLatestUnpaidBill(List<CustomerParamCriterion> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return null;
        }

        String billTable = id(postgres.getTables().getBillDetails());
        String billIdCol = id(postgres.getBillColumns().getBillId());
        String customerParamNameCol = id(postgres.getBillColumns().getCustomerParamName());
        String customerParamTypeCol = id(postgres.getBillColumns().getCustomerParamType());
        String customerParamValueCol = id(postgres.getBillColumns().getCustomerParamValue());
        String billAmountCol = id(postgres.getBillColumns().getBillAmount());
        String billDateCol = id(postgres.getBillColumns().getBillDate());
        String dueDateCol = id(postgres.getBillColumns().getDueDate());
        String billNumberCol = id(postgres.getBillColumns().getBillNumber());
        String billPeriodCol = id(postgres.getBillColumns().getBillPeriod());
        String billStatusCol = id(postgres.getBillColumns().getBillStatus());
        String additionalInfoCol = id(postgres.getBillColumns().getAdditionalInfo());
        String createdAtCol = id(postgres.getBillColumns().getCreatedAt());
        String updatedAtCol = id(postgres.getBillColumns().getUpdatedAt());

        StringBuilder sql = new StringBuilder("""
                SELECT %s AS bill_id, %s AS customer_param_name, %s AS customer_param_type, %s AS customer_param_value,
                       %s AS bill_amount, %s AS bill_date, %s AS due_date, %s AS bill_number, %s AS bill_period,
                       %s AS bill_status, %s AS additional_info, %s AS created_at, %s AS updated_at
                FROM %s
                WHERE %s = 'UNPAID'
                """.formatted(
                billIdCol, customerParamNameCol, customerParamTypeCol, customerParamValueCol,
                billAmountCol, billDateCol, dueDateCol, billNumberCol, billPeriodCol, billStatusCol,
                additionalInfoCol, createdAtCol, updatedAtCol, billTable, billStatusCol
        ));

        MapSqlParameterSource params = new MapSqlParameterSource();
        appendCriteriaClause(criteria, sql, params, customerParamNameCol, customerParamValueCol);

        sql.append("""
                    ORDER BY %s NULLS LAST, %s DESC, %s DESC
                    LIMIT 1
                """.formatted(dueDateCol, billDateCol, billIdCol));

        List<BillDetails> list = jdbcNamedTemplate.query(sql.toString(), params, (rs, rowNum) -> mapBillDetails(rs));
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    @Transactional
    public boolean markBillPaidAndRecordTransaction(List<CustomerParamCriterion> criteria, PaymentUpdateRequest paymentUpdateRequest) {
        if (criteria == null || criteria.isEmpty() || paymentUpdateRequest == null) {
            return false;
        }

        String billTable = id(postgres.getTables().getBillDetails());
        String paymentTable = id(postgres.getTables().getPaymentTransactions());
        String billIdCol = id(postgres.getBillColumns().getBillId());
        String customerParamNameCol = id(postgres.getBillColumns().getCustomerParamName());
        String customerParamValueCol = id(postgres.getBillColumns().getCustomerParamValue());
        String billStatusCol = id(postgres.getBillColumns().getBillStatus());
        String updatedAtCol = id(postgres.getBillColumns().getUpdatedAt());
        String dueDateCol = id(postgres.getBillColumns().getDueDate());
        String billDateCol = id(postgres.getBillColumns().getBillDate());
        String paymentBillIdCol = id(postgres.getPaymentColumns().getBillId());
        String paymentTxnRefCol = id(postgres.getPaymentColumns().getBbpsTxnRef());
        String paymentAmountCol = id(postgres.getPaymentColumns().getAmountPaid());
        String paymentModeCol = id(postgres.getPaymentColumns().getPaymentMode());

        MapSqlParameterSource updateParams = new MapSqlParameterSource();
        StringBuilder subWhere = new StringBuilder("%s = 'UNPAID'".formatted(billStatusCol));
        appendCriteriaClause(criteria, subWhere, updateParams, customerParamNameCol, customerParamValueCol);

        String updateSql = """
                UPDATE %s
                SET %s = 'PAID',
                    %s = now()
                WHERE %s = (
                    SELECT %s FROM %s
                    WHERE %s
                    ORDER BY %s NULLS LAST, %s DESC, %s DESC
                    LIMIT 1
                )
                """.formatted(
                billTable,
                billStatusCol,
                updatedAtCol,
                billIdCol,
                billIdCol,
                billTable,
                subWhere,
                dueDateCol,
                billDateCol,
                billIdCol
        );

        int updatedRows = jdbcNamedTemplate.update(updateSql, updateParams);
        if (updatedRows <= 0) {
            return false;
        }

        String insertSql = """
                INSERT INTO %s (%s, %s, %s, %s)
                VALUES (:billId, :bbpsTxnRef, :amountPaid, :paymentMode)
                """.formatted(paymentTable, paymentBillIdCol, paymentTxnRefCol, paymentAmountCol, paymentModeCol);

        MapSqlParameterSource insertParams = new MapSqlParameterSource()
                .addValue("billId", paymentUpdateRequest.getBillId())
                .addValue("bbpsTxnRef", paymentUpdateRequest.getBbpsTxnRef())
                .addValue("amountPaid", paymentUpdateRequest.getAmountPaid())
                .addValue("paymentMode", paymentUpdateRequest.getPaymentMode());

        int inserted = jdbcNamedTemplate.update(insertSql, insertParams);
        return updatedRows > 0 && inserted > 0;
    }

    private void appendCriteriaClause(List<CustomerParamCriterion> criteria,
                                      StringBuilder sql,
                                      MapSqlParameterSource params,
                                      String customerParamNameCol,
                                      String customerParamValueCol) {
        boolean addedAny = false;
        for (int i = 0; i < criteria.size(); i++) {
            CustomerParamCriterion criterion = criteria.get(i);
            if (criterion == null) {
                continue;
            }

            String nKey = "name" + i;
            String vKey = "value" + i;
            params.addValue(nKey, criterion.getName());
            params.addValue(vKey, criterion.getValue());

            sql.append(addedAny ? " OR " : " AND (");
            sql.append("(").append(customerParamNameCol).append(" = :").append(nKey)
                    .append(" AND ").append(customerParamValueCol).append(" = :").append(vKey).append(")");
            addedAny = true;
        }
        if (addedAny) {
            sql.append(")");
        }
    }

    private BillDetails mapBillDetails(ResultSet rs) throws SQLException {
        BillDetails b = new BillDetails();
        b.setBillId(rs.getLong("bill_id"));
        b.setCustomerParamName(rs.getString("customer_param_name"));
        b.setCustomerParamType(rs.getString("customer_param_type"));
        b.setCustomerParamValue(rs.getString("customer_param_value"));
        b.setBillAmount(rs.getBigDecimal("bill_amount"));
        var billDate = rs.getDate("bill_date");
        if (billDate != null) {
            b.setBillDate(billDate.toLocalDate());
        }
        var dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            b.setDueDate(dueDate.toLocalDate());
        }
        b.setBillNumber(rs.getString("bill_number"));
        b.setBillPeriod(rs.getString("bill_period"));
        b.setBillStatus(rs.getString("bill_status"));
        b.setAdditionalInfo(rs.getString("additional_info"));
        var created = rs.getTimestamp("created_at");
        if (created != null) {
            b.setCreatedAt(created.toLocalDateTime());
        }
        var updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            b.setUpdatedAt(updated.toLocalDateTime());
        }
        return b;
    }

    private String id(String configuredValue) {
        if (configuredValue == null || configuredValue.isBlank()) {
            throw new IllegalArgumentException("Postgres table/column mapping value must not be blank");
        }
        String value = configuredValue.trim();
        if (!IDENTIFIER_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid postgres identifier: " + value);
        }
        return value;
    }
}
