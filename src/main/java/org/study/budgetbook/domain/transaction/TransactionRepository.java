package org.study.budgetbook.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom  {
}
