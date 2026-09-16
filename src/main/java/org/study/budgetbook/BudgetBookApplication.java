package org.study.budgetbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // BaseEntity의 @CreatedDate, @LastModifiedDate 활성화
@SpringBootApplication
public class BudgetBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetBookApplication.class, args);
    }

}
