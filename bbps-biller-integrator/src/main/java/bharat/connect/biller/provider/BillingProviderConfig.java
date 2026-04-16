package bharat.connect.biller.provider;

import bharat.connect.biller.provider.impl.CsvBillingProvider;
import bharat.connect.biller.provider.impl.ExcelBillingProvider;
import bharat.connect.biller.provider.impl.PostgresBillingProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
@EnableConfigurationProperties(BillingProviderProperties.class)
public class BillingProviderConfig {

    @Bean
    public BillingProvider billingProvider(BillingProviderProperties properties,
                                           ObjectProvider<NamedParameterJdbcTemplate> jdbcNamedTemplateProvider) {
        String type = properties.getType() == null ? "postgres" : properties.getType().trim().toLowerCase();
        return switch (type) {
            case "csv" -> new CsvBillingProvider(properties);
            case "excel" -> new ExcelBillingProvider(properties);
            case "postgres" -> new PostgresBillingProvider(requireJdbcTemplate(jdbcNamedTemplateProvider.getIfAvailable()), properties);
            default -> throw new IllegalArgumentException("Unsupported billing.provider.type: " + type);
        };
    }

    private NamedParameterJdbcTemplate requireJdbcTemplate(NamedParameterJdbcTemplate jdbcNamedTemplate) {
        if (jdbcNamedTemplate == null) {
            throw new IllegalStateException("NamedParameterJdbcTemplate bean missing for postgres provider.");
        }
        return jdbcNamedTemplate;
    }
}
