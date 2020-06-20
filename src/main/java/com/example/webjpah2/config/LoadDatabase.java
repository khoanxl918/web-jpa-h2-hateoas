package com.example.webjpah2.config;

import com.example.webjpah2.payroll.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    public static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDB(EmployeeRepository empRepo, OrderRepository ordRepo) {
        return args -> {
          logger.info("Preloading... " + empRepo.save(new Employee("burglar", "Biblo",  "Baggins")));
          logger.info("Preloading... " + empRepo.save(new Employee("thief", "Fondor", "Baggins")));

          ordRepo.save(new Order("Macbook Pro", Status.COMPLETED));
          ordRepo.save(new Order("iPhone", Status.IN_PROGRESS));

          ordRepo.findAll().forEach(order -> {
              logger.info("Preloaded " + order);
          });

        };
    }
}
