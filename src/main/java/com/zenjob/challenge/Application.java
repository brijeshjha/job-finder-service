package com.zenjob.challenge;

import com.zenjob.challenge.constants.APIConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(info = @Info(title = APIConstants.APPLICATION_TITLE, version = APIConstants.APPLICATION_VERSION, description = APIConstants.APPLICATION_DESCRIPTION))
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
