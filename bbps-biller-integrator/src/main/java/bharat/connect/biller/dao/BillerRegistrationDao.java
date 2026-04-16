package bharat.connect.biller.dao;

import bharat.connect.biller.dto.BillerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class BillerRegistrationDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void saveRegisteredBiller(String generatedBillerId, BillerRegistrationRequest request,
                                     String mockFetchUrl, String mockPaymentUrl) {
        String sql = "INSERT INTO registered_billers " +
                "(biller_id, biller_ref_id, entity_name, bill_category, customer_params, mock_fetch_url, mock_payment_url) " +
                "VALUES (:billerId, :refId, :name, :category, :params, :fetchUrl, :paymentUrl)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("billerId", generatedBillerId)
                .addValue("refId", request.getBillerRefId())
                .addValue("name", request.getEntityName())
                .addValue("category", request.getBillCategory())
                .addValue("params", createJsonbObject(request.getCustomerParams()))
                .addValue("fetchUrl", mockFetchUrl)
                .addValue("paymentUrl", mockPaymentUrl);

        jdbcTemplate.update(sql, params);
    }

    private PGobject createJsonbObject(Object obj) {
        try {
            PGobject pgObj = new PGobject();
            pgObj.setType("jsonb");
            pgObj.setValue(objectMapper.writeValueAsString(obj));
            return pgObj;
        } catch (SQLException | JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSONB", e);
        }
    }
}
