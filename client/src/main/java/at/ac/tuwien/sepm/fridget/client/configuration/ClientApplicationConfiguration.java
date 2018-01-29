package at.ac.tuwien.sepm.fridget.client.configuration;

import at.ac.tuwien.sepm.fridget.common.util.StringCurrencyConverter;
import at.ac.tuwien.sepm.fridget.common.util.StringMoneyConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientApplicationConfiguration {

    /**
     * Helper to convert strings to BigDecimal and the other way round
     *
     * @return the converter instance
     */
    @Bean
    public StringMoneyConverter stringMoneyConverter() {
        return new StringMoneyConverter();
    }

    /**
     * Helper to convert strings to currency exchange rates and the other way round
     *
     * @return
     */
    @Bean
    public StringCurrencyConverter stringCurrencyConverter() {
        return new StringCurrencyConverter();
    }

    /**
     * Mapper to map UI Models from UI layer to service layer
     *
     * @return the mapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
