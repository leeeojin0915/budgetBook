package org.study.budgetbook.domain.budget;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.study.budgetbook.domain.category.Category;
import org.study.budgetbook.domain.common.BaseEntity;
import org.study.budgetbook.domain.user.User;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="budget", uniqueConstraints = {
        // 같은 사용자가 동일 카테고리,연월에 예산을 중복 등록 방지
        @UniqueConstraint(columnNames = {"user_id", "category_id", "year", "month"})
})
public class Budget extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    private BigDecimal amount;
    private int year;
    private int month;

    @Builder
    public Budget(User user, Category category, BigDecimal amount, int year, int month) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.year = year;
        this.month = month;
    }

    // 예산 금액만 수정 가능 (연월·카테고리 변경은 새 예산 등록으로 처리)
    public void update(BigDecimal amount){
        this.amount=amount;
    }
}
