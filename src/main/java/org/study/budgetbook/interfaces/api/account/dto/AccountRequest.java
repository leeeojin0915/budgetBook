package org.study.budgetbook.interfaces.api.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.study.budgetbook.domain.account.Account;

import java.math.BigDecimal;

@Getter
public class AccountRequest {
    @NotBlank(message = "계좌 이름은 필수 입니다.")
    @Size(max = 50, message = "계좌 이름은 50자 이하여야 합니다.")
    private String name;

    @NotNull(message = "계좌 타입은 필수 입니다.")
    private Account.AccountType type;

    @PositiveOrZero(message = "초기 잔액은 0 이상이어야 합니다.")
    private BigDecimal balance;

    private String currency;
}
