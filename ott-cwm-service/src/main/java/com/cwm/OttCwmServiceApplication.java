package com.cwm;

import com.cwm.config.CwmProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CwmProperties.class)
public class OttCwmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OttCwmServiceApplication.class, args);
    }
}
