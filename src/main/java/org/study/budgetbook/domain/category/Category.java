package org.study.budgetbook.domain.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.study.budgetbook.domain.common.BaseEntity;
import org.study.budgetbook.domain.user.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String name;
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
    private boolean deleted = false;

    public enum CategoryType{
        INCOME, // 수입
        EXPENSE // 지출
    }

    @Builder
    public Category(User user, String name, CategoryType categoryType) {
        this.user = user;
        this.name = name;
        this.categoryType = categoryType;
    }

    /*
    * 도메인 메서드
    * */
    public void update(String name){
        this.name=name;
    }
    public void deleted(){
        this.deleted = true;
    }
}
