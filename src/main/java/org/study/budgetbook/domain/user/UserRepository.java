package org.study.budgetbook.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    // 삭제 되지 않은 사용자만 이메일로 조회 (로그인 ,중복체크에 사용)
    Optional<User> findUserByEmailAndDeletedYnFalse(String email);
    // 회원가입 시 이메일 중복 체크
    boolean existsByEmail(String email);
}
