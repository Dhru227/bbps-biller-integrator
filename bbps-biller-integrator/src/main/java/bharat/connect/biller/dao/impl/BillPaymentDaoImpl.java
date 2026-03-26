package bharat.connect.biller.dao.impl;

import bharat.connect.biller.dao.BillPaymentDao;
import org.bbps.schema.CustomerParamsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class BillPaymentDaoImpl implements BillPaymentDao {

    @Autowired
    @Qualifier(value = "jdbcNamedTemplate")
    private NamedParameterJdbcTemplate jdbcNamedTemplate;

    @Transactional
    @Override
    public boolean markBillPaidAndRecordTransaction(CustomerParamsType customerParams,
                                                     long billId,
                                                     String bbpsTxnRef,
                                                     BigDecimal amountPaid,
                                                     String paymentMode) {
        List<CustomerParamsType.Tag> tags = customerParams.getTags();
        if (tags == null || tags.isEmpty()) {
            return false;
        }

        MapSqlParameterSource updateParams = new MapSqlParameterSource();

        // Build customer-params matching clause (same pattern as BillFetchDaoImpl)
        StringBuilder subWhere = new StringBuilder("bill_status = 'UNPAID'");
        boolean addedAny = false;
        for (int i = 0; i < tags.size(); i++) {
            CustomerParamsType.Tag t = tags.get(i);
            if (t == null) continue;
            String name = (t.getName() == null) ? null : t.getName().trim();
            String value = (t.getValue() == null) ? null : t.getValue().trim();
            if (name == null || name.isEmpty() || value == null || value.isEmpty()) continue;

            String nKey = "name" + i;
            String vKey = "value" + i;
            updateParams.addValue(nKey, name);
            updateParams.addValue(vKey, value);

            subWhere.append(addedAny ? " OR " : " AND (");
            subWhere.append("(customer_param_name = :").append(nKey)
                    .append(" AND customer_param_value = :").append(vKey).append(")");
            addedAny = true;
        }

        if (!addedAny) {
            return false;
        }
        subWhere.append(")");

        // Step 1: UPDATE bill_details to PAID, searched by customer params
        String updateSql = """
                UPDATE bill_details
                SET bill_status = 'PAID',
                    updated_at = now()
                WHERE bill_id = (
                    SELECT bill_id FROM bill_details
                    WHERE %s
                    ORDER BY due_date NULLS LAST, bill_date DESC, bill_id DESC
                    LIMIT 1
                )
                """.formatted(subWhere);

        int updatedRows = jdbcNamedTemplate.update(updateSql, updateParams);
        if (updatedRows <= 0) {
            System.out.println("markBillPaidAndRecordTransaction: UPDATE matched 0 rows. No UNPAID bill for given customer params.");
            return false;
        }
        System.out.println("markBillPaidAndRecordTransaction: bill_details updated to PAID (" + updatedRows + " row(s)).");

        // Step 2: INSERT into payment_transactions (same transaction — rolls back UPDATE if this fails)
        String insertSql = """
                INSERT INTO payment_transactions (bill_id, bbps_txn_ref, amount_paid, payment_mode)
                VALUES (:billId, :bbpsTxnRef, :amountPaid, :paymentMode)
                """;

        MapSqlParameterSource insertParams = new MapSqlParameterSource()
                .addValue("billId", billId)
                .addValue("bbpsTxnRef", bbpsTxnRef)
                .addValue("amountPaid", amountPaid)
                .addValue("paymentMode", paymentMode);

        int inserted = jdbcNamedTemplate.update(insertSql, insertParams);
        System.out.println("markBillPaidAndRecordTransaction: payment_transactions inserted=" + inserted
                + " for bill_id=" + billId + ", bbpsTxnRef=" + bbpsTxnRef);

        return updatedRows > 0 && inserted > 0;
    }
}
