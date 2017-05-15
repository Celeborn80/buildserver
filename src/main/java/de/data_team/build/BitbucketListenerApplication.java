package de.data_team.build;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class BitbucketListenerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BitbucketListenerApplication.class, args);
    }
}
