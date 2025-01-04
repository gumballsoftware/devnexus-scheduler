package org.gumball.ajug.devnexus.desktop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ApplicationProperties.class
})
public class ApplicationConfiguration {

    @Autowired
    ApplicationProperties applicationProperties;
}
