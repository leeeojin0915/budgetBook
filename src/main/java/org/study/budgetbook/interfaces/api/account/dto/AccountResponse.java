package org.study.budgetbook.interfaces.api.account.dto;

import lombok.Builder;
import lombok.Getter;
import org.study.budgetbook.domain.account.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AccountResponse {
    private Long id;
    private String name;
    private BigDecimal balance;
    private String type;
    private String currency;
    private LocalDateTime createdAt;

    public static AccountResponse from(Account account){
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .type(account.getType().name())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
