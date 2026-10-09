package org.study.budgetbook.interfaces.api.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class BalanceUpdateRequest {
    /*
    * 잔액 직접 수정 요청 금액
    * 수입이면 양수, 지출이면 음수
    * */
    @NotNull(message = "금액은 필수 입니다.")
    private BigDecimal amount;
}
