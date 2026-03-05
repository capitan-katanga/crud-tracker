package com.expense.tracker.crudtracker.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Expense Tracker API",
                version = "0.1.0",
                description = "API for registering financial transactions/operations. Extensible by transaction type via strategy pattern for future scalability."
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development")
        }
)
public class OpenApiConfig {
}
