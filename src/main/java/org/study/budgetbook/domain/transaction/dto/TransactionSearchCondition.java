package org.study.budgetbook.domain.transaction.dto;

import lombok.Builder;
import lombok.Getter;
import org.study.budgetbook.domain.transaction.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class TransactionSearchCondition {
    private Long accountId;                     // 특정 계좌 필터
    private Long categoryId;                    // 특정 카테고리 필터
    private Transaction.TransactionType type;   // 수입/지출 필터
    private LocalDate startDate;                // 조회 시작일
    private LocalDate endDate;                  // 조회 종료일
    private BigDecimal minAmount;               // 최소 금액
    private BigDecimal maxAmount;               // 최대 금액
}
