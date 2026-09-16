package org.study.budgetbook.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 사용자의 전체 카테고리 조회
    List<Category> findAllByUserId(Long userId);

    // 수입/지출 타입별 카테고리 조회 (거래 등록 시 타입에 맞는 카테고리 선택용)
    List<Category> findByUserIdAndCategoryTypeAndDeletedFalse(Long userId, Category.CategoryType categoryType);
}
