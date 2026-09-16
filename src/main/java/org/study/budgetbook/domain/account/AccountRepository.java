package org.study.budgetbook.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.study.budgetbook.domain.user.User;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 특정 사용자의 삭제되지 않은 계좌 목록 조회
    List<Account> findByUser_IdAndDeletedFalse(Long userId);
}
