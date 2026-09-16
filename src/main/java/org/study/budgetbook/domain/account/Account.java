package org.study.budgetbook.domain.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.study.budgetbook.domain.user.User;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 지연 로딩 : Account 조회시 User를 즉시 조인하지 않음 -> 불필요한 쿼리 방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    private String name;
    private BigDecimal balance = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private String currency = "KRW";
    private boolean deleted = false;

    public enum AccountType{
        CHECKING, // 입출금
        SAVINGS, // 저축
        CARD, // 신용카드
        INVESTMENT // 주식/펀드
    }

    @Builder
    public Account(User user, String name, BigDecimal balance, AccountType accountType, String currency) {
        this.user = user;
        this.name = name;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.accountType = accountType;
        this.currency = currency != null ? currency : "KRW";
    }


    /*
    * 도메인 메서드
    * */
    public void updateInfo(String name, AccountType accountType) {
        this.name = name;
        this.accountType = accountType;
    }

    public void adjustBalance(BigDecimal amount){
        this.balance = this.balance.add(amount);
    }

    public void delete(){
        this.deleted = true;
    }
}
