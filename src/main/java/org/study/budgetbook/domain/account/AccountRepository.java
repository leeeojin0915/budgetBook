package org.study.budgetbook.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // 특정 사용자의 삭제되지 않은 계좌 목록 조회
    List<Account> findByUserIdAndDeletedFalse(Long userId);

    // 계좌 단건 조회 (본인 소유 + 미삭제 동시 검증)
    Optional<Account> findByUserIdAndIdAndDeletedFalse(Long userId, Long accountId);
}
