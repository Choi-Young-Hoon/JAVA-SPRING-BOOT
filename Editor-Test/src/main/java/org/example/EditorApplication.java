package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EditorApplication {
    public static void main(String[] args) {
        SpringApplication.run(EditorApplication.class, args);
    }
}