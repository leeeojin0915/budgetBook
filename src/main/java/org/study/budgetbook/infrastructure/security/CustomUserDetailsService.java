package org.study.budgetbook.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.study.budgetbook.domain.user.User;
import org.study.budgetbook.domain.user.UserRepository;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    /**
     * Spring Security가 폼 로그인 또는 AuthenticationManager 인증 시 호출
     * 본 프로젝트에서는 AuthService.login()에서 직접 호출
     *
     * @param email getUsername()이 반환하는 값 (email)
     * @throws UsernameNotFoundException 사용자 없을 때 → 401 응답으로 변환됨
     *
     * 보안 주의: 존재하지 않는 이메일과 비밀번호 틀림을 같은 메시지로 처리
     * → 공격자가 이메일 존재 여부를 알 수 없게 함
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmailAndDeletedYnFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다." + email));
        return new CustomUserDetails(user) {
        };
    }
}
