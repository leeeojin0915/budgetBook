package org.study.budgetbook.application.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.budgetbook.domain.user.User;
import org.study.budgetbook.domain.user.UserRepository;
import org.study.budgetbook.interfaces.api.user.dto.UpdateNicknameRequest;
import org.study.budgetbook.interfaces.api.user.dto.UpdatePasswordRequest;
import org.study.budgetbook.interfaces.api.user.dto.UserResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 내 정보 조회
     *
     * @param userId JwtAuthenticationFilter에서 SecurityContext에 저장한 userId
     *               → Controller에서 @AuthenticationPrincipal로 꺼내서 전달
     *
     * deleted = false 조건으로 조회하는 이유:
     * → 탈퇴한 사용자는 토큰이 남아있어도 정보 조회 불가 처리
     */
    public UserResponse getMe(Long userId) {
        User user = findActiveUser(userId);
        return UserResponse.from(user);
    }

    /*
     * 닉네임 수정
     * */
    @Transactional
    public UserResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        User  user = findActiveUser(userId);
        user.updateNickname(request.getNickname());
        return UserResponse.from(user);
    }

    /**
     * 비밀번호 변경
     *
     * 현재 비밀번호 검증 후 새 비밀번호로 교체
     * 현재 비밀번호 확인 이유:
     * → AccessToken이 탈취된 상황에서도 비밀번호 무단 변경 방지
     */
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User  user = findActiveUser(userId);

        // 입력한 비밀번호와 현재 등록된 비밀번호가 일치하는지 확인
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 현재 비밀번호와 새 비밀번호가 일치하는지 확인
        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야합니다.");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    /**
     * 회원 탈퇴 (소프트 삭제)
     *
     * 실제 row를 삭제하지 않는 이유:
     * → 탈퇴 후에도 거래내역 등 연관 데이터의 무결성 유지
     * → 탈퇴 이력 보존 (고객 문의, 분쟁 대응)
     * → 재가입 시 이전 데이터 복구 가능성 대비
     */
    @Transactional
    public void delete(Long userId) {
        User  user = findActiveUser(userId);
        user.delete(); // deleted = true로 변경
    }


    private User findActiveUser(Long userId) {
        log.debug(userId.toString());
        return userRepository.findByIdAndDeletedYnFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
