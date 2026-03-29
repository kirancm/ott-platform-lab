package com.opcon.bff;

import com.opcon.bff.config.DownstreamProperties;
import com.opcon.bff.config.OpConWebClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({DownstreamProperties.class, OpConWebClientProperties.class})
public class OpConControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpConControllerApplication.class, args);
    }
}
