package org.study.budgetbook.domain.transaction;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.study.budgetbook.domain.account.Account;
import org.study.budgetbook.domain.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transaction",
indexes = {
        // 계좌별 거래내역 조회 시 자주 사용 → 인덱스 추가
        @Index(name = "idx_transaction_account", columnList = "account_id"),
        // 날짜 범위 필터링이 핵심 기능 → 인덱스 추가
        @Index(name = "idx_transaction_date", columnList = "transaction_date")
})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private LocalDate transactionDate;
    private String memo;

    public enum TransactionType {
        INCOME, // 수입
        EXPENSE // 지출
    }

    @Builder
    public Transaction(Account account, Category category, BigDecimal amount, TransactionType transactionType,
                       LocalDate transactionDate, String memo) {
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.memo = memo;
    }

    // 도메인 메서드
    // amount나 type 변경 시 Account.balance도 함께 재계산해야 함 → Service에서 처리
    public void update(Category category, BigDecimal amount, LocalDate transactionDate, String memo){
        this.category = category;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.memo = memo;
    }

}
