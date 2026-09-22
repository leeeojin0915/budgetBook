package org.study.budgetbook.domain.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.study.budgetbook.domain.transaction.dto.TransactionSearchCondition;


// QueryDSL 구현체와 연결되는 인터페이스
// infrastructure/persistence/TransactionRepositoryImpl.java 에서 구현
public interface TransactionRepositoryCustom {
    Page<Transaction> search(Long userId, TransactionSearchCondition condition, Pageable pageable);
}
