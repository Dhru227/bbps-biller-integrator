package bharat.connect.biller.dao.impl;

import bharat.connect.biller.dao.BillFetchDao;
import bharat.connect.biller.model.BillDetails;
import org.bbps.schema.CustomerParamsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BillFetchDaoImpl implements BillFetchDao {

    @Autowired
    @Qualifier(value = "jdbcNamedTemplate")
    private NamedParameterJdbcTemplate jdbcNamedTemplate;

    @Override
    public BillDetails findLatestUnpaidByParam(String paramName, String paramValue) {
        String sql = """
                SELECT bill_id, customer_param_name, customer_param_type, customer_param_value,
                       bill_amount, bill_date, due_date, bill_number, bill_period, bill_status,
                       additional_info, created_at, updated_at
                FROM bill_details
                WHERE customer_param_name = :paramName
                  AND customer_param_value = :paramValue
                  AND bill_status = 'UNPAID'
                ORDER BY due_date NULLS LAST, bill_date DESC, bill_id DESC
                LIMIT 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("paramName", paramName).addValue("paramValue", paramValue);

        List<BillDetails> list = jdbcNamedTemplate.query(sql, params, (rs, rowNum) -> {
            BillDetails b = new BillDetails();
            b.setBillId((long) rs.getInt("bill_id"));
            b.setCustomerParamName(rs.getString("customer_param_name"));
            b.setCustomerParamType(rs.getString("customer_param_type"));
            b.setCustomerParamValue(rs.getString("customer_param_value"));
            b.setBillAmount(rs.getBigDecimal("bill_amount"));
            var billDate = rs.getDate("bill_date");
            if (billDate != null) b.setBillDate(billDate.toLocalDate());
            var dueDate = rs.getDate("due_date");
            if (dueDate != null) b.setDueDate(dueDate.toLocalDate());
            b.setBillNumber(rs.getString("bill_number"));
            b.setBillPeriod(rs.getString("bill_period"));
            b.setBillStatus(rs.getString("bill_status"));
            b.setAdditionalInfo(rs.getString("additional_info"));
            var created = rs.getTimestamp("created_at");
            if (created != null) b.setCreatedAt(created.toLocalDateTime());
            var updated = rs.getTimestamp("updated_at");
            if (updated != null) b.setUpdatedAt(updated.toLocalDateTime());
            return b;
        });
        if (list.isEmpty()) return null;
        return list.get(0);
    }
    @Override
    public BillDetails findLatestUnpaidBillByCustomerParams(CustomerParamsType customerParamsType) {
        List<CustomerParamsType.Tag> tags = customerParamsType.getTags();
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder("""
                    SELECT bill_id, customer_param_name, customer_param_type, customer_param_value,
                           bill_amount, bill_date, due_date, bill_number, bill_period, bill_status,
                           additional_info, created_at, updated_at
                    FROM bill_details
                    WHERE bill_status = 'UNPAID'
                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // Build the OR group: ( (name=:n0 AND value=:v0) OR (name=:n1 AND value=:v1) OR ... )
        boolean addedAny = false;
        for (int i = 0; i < tags.size(); i++) {
            CustomerParamsType.Tag t = tags.get(i);
            if (t == null) continue;
            String name = (t.getName() == null) ? null : t.getName().trim();
            String value = (t.getValue() == null) ? null : t.getValue().trim();
            if (name == null || name.isEmpty() || value == null || value.isEmpty()) continue;

            String nKey = "name" + i;
            String vKey = "value" + i;
            params.addValue(nKey, name);
            params.addValue(vKey, value);

            sql.append(addedAny ? " OR " : " AND (");
            sql.append("(customer_param_name = :").append(nKey).append(" AND customer_param_value = :").append(vKey).append(")");
            addedAny = true;
        }

        if (addedAny) {
            sql.append(")");
        } else {
            // All tags were blank/null — nothing meaningful to filter on
            return null;
        }

        sql.append("""
                    ORDER BY due_date NULLS LAST, bill_date DESC, bill_id DESC
                    LIMIT 1
                """);

        List<BillDetails> list = jdbcNamedTemplate.query(sql.toString(), params, (rs, rowNum) -> {
            BillDetails b = new BillDetails();
            b.setBillId((long) rs.getInt("bill_id"));
            b.setCustomerParamName(rs.getString("customer_param_name"));
            b.setCustomerParamType(rs.getString("customer_param_type"));
            b.setCustomerParamValue(rs.getString("customer_param_value"));
            b.setBillAmount(rs.getBigDecimal("bill_amount"));

            var billDate = rs.getDate("bill_date");
            if (billDate != null) b.setBillDate(billDate.toLocalDate());
            var dueDate = rs.getDate("due_date");
            if (dueDate != null) b.setDueDate(dueDate.toLocalDate());

            b.setBillNumber(rs.getString("bill_number"));
            b.setBillPeriod(rs.getString("bill_period"));
            b.setBillStatus(rs.getString("bill_status"));
            b.setAdditionalInfo(rs.getString("additional_info"));

            var created = rs.getTimestamp("created_at");
            if (created != null) b.setCreatedAt(created.toLocalDateTime());
            var updated = rs.getTimestamp("updated_at");
            if (updated != null) b.setUpdatedAt(updated.toLocalDateTime());

            return b;
        });
        return list.isEmpty() ? null : list.get(0);
    }
}
