package org.study.budgetbook.application.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.budgetbook.domain.user.User;
import org.study.budgetbook.domain.user.UserRepository;
import org.study.budgetbook.infrastructure.security.JwtTokenProvider;
import org.study.budgetbook.interfaces.api.auth.dto.LoginRequest;
import org.study.budgetbook.interfaces.api.auth.dto.SignupRequest;
import org.study.budgetbook.interfaces.api.auth.dto.TokenResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /*
    * 회원가입
    * @Transactional
    * */
    @Transactional
    public void signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(User.Role.ROLE_USER)
                .build();
        userRepository.save(user);
    }

    /*
    * 로그인 -> AccessToken + RefreshToken 발급
    *
    * 보안 > 이메일 존재 여부와 비밀번호 틀림을 동일한 메시지로 처리
    * -> "이메일이 없습니다" VS "비밀번호가 틀렸습니다"를 구분하면
    * 공격자가 계정 존재여부를 알 수 있음.
    * */
    public TokenResponse login(LoginRequest request){
        User user = userRepository.findUserByEmailAndDeletedYnFalse(request.getEmail())
                .orElseThrow(()->
                        new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        /*
        * BCrypt 비밀번호 검증
        * matches(평문, 해시) > 내부적으로 평문을 해시하여 저장된 해시와 비교
        * */
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            log.debug("old:{}, new:{}",request.getClass(), user.getPassword());
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getRole().name());

        /*
        * TODO: RefreshToken 저장소 구현
        *
        * 현행 > RefreshToken을 사용자에게만 발급하고 서버에서 저장하지않음
        * 문제점 : 강제 로그아웃 불가, 탈취된 RefreshToken으로 무한 재발급 가능
        *
        * 해결방안 >
        * - Redis : TTL 자동 관리, 빠른 조회
        * - DB: 별도 refresh_tokens 테이블 관리
        * */
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /*
    * AccessToken 재발급
    *
    * 클라이언트 흐름
    * 1. API호출 -> 401 응답 (AccessToken 만료)
    * 2. POST /api/auth/refresh 호출 (RefreshToken 전송)
    * 3. 새 AccessToken 수령 -> 원래 API 재호출
    * */
    public TokenResponse refresh(String refreshToken){
        if(!jwtTokenProvider.validate(refreshToken)){
            throw new IllegalArgumentException("유효하지않은 토큰입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);

        /*
        * TODO : 저장된 RefreshToken과 일치 여부 확인
        *
        * RefreshToken Rotation 전략
        * - 재발급시 RefreshToken도 새로 발급
        * - 이전 RefreshToken은 즉시 무효
        * -> 탈취 된 토큰이 먼저 사용되면 원래 사용자 재발급시 감지가능
        * */

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // refreshToken은 만료 전까지 재사용
                .build();
    }
}
