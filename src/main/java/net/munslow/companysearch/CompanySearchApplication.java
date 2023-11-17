package net.munslow.companysearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "net.munslow.companysearch")
public class CompanySearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompanySearchApplication.class, args);
    }
}
