package org.gumball.ajug.devnexus.desktop.config;

import org.gumball.ajug.devnexus.desktop.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        IngestProperties.class
})
public class ApplicationConfiguration {

    @Autowired
    IngestProperties ingestProperties;

    @Bean
    public IngestService ingestService() {
        return new IngestService(ingestProperties);
    }
}
