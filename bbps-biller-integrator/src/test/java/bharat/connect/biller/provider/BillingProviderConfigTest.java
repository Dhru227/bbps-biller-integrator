package bharat.connect.biller.provider;

import bharat.connect.biller.provider.impl.CsvBillingProvider;
import bharat.connect.biller.provider.impl.ExcelBillingProvider;
import bharat.connect.biller.provider.impl.PostgresBillingProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillingProviderConfigTest {

    private final BillingProviderConfig config = new BillingProviderConfig();
    private final NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
    private final ObjectProvider<NamedParameterJdbcTemplate> jdbcProvider = mock(ObjectProvider.class);

    @Test
    void shouldBuildPostgresProvider() {
        BillingProviderProperties properties = new BillingProviderProperties();
        properties.setType("postgres");
        when(jdbcProvider.getIfAvailable()).thenReturn(jdbc);
        BillingProvider provider = config.billingProvider(properties, jdbcProvider);
        assertInstanceOf(PostgresBillingProvider.class, provider);
    }

    @Test
    void shouldBuildCsvProvider() {
        BillingProviderProperties properties = new BillingProviderProperties();
        properties.setType("csv");
        BillingProvider provider = config.billingProvider(properties, jdbcProvider);
        assertInstanceOf(CsvBillingProvider.class, provider);
    }

    @Test
    void shouldBuildExcelProvider() {
        BillingProviderProperties properties = new BillingProviderProperties();
        properties.setType("excel");
        BillingProvider provider = config.billingProvider(properties, jdbcProvider);
        assertInstanceOf(ExcelBillingProvider.class, provider);
    }
}
