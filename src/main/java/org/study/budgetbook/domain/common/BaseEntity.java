package org.study.budgetbook.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 테이블로 생성되지 않고, 자식 Entity에 필드만 상속
@EntityListeners(AuditingEntityListener.class) // 생성/시간 자동 기록
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false) // 최초 생성 후 변경 불가
    private LocalDateTime createAt;
    @LastModifiedDate // 수정 시 자동 갱신
    private LocalDateTime updateAt;
}
