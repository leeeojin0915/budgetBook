package org.study.budgetbook.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // 특정 월의 전체 예산 목록 (예산 현황 대시보드용)

    List<Budget> findAllByMonth(int month);

    // 특정 카테고리,연월의 예산 단건 조회 (중복 등록 방지, 수정 시 사용)

    Optional<Budget> findByUserIdAndCategoryIdAndYearAndMonth(Long user_id, Long category_id, int year, int month);
}
