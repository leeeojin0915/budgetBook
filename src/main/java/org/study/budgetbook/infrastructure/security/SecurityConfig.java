package org.study.budgetbook.infrastructure.security;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // Spring Security 활성화
@EnableMethodSecurity // @PreAuthorize, @PostAuthrize 등 메서드 보안 활성화
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         * CSRF(Cross-Site Request Forgery) 비활성화
         * CSRF는 브라우저 쿠키 기반 인증에서 필요
         * JWT는 Authorization 헤더로 전송 -> 브라우저가 자동 첨부 안 하 -> CSRF 위협 없음
         * */
        http.csrf(AbstractHttpConfigurer::disable)

                /**
                 * 세션 정책: STATELESS
                 * JWT는 서버가 상태를 저장하지 않는 무상태(Stateless) 방식
                 * → 세션을 생성하거나 사용하지 않음
                 * → 수평 확장(서버 증설) 시 세션 공유 문제 없음
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /**
                 * 요청 경로별 인가 규칙
                 * 위에서 아래 순서로 매칭 → 구체적인 경로를 위에 배치
                 */
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능> 회원가입, 로그인, 토큰 발급
                        .requestMatchers("/api/auth/**").permitAll()
                        // ROLE_ADMIN 권한 보유자만 접근가능
                        // hasRole("ADMIN") -> 내부적으로 "ROLE_ADMIN" 확인
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 위 규칙에 해당하지 않는 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                /**
                 * JWT 필터 등록
                 * UsernamePasswordAuthenticationFilter 앞에 배치하는 이유:
                 * → Spring Security 기본 폼 로그인 필터보다 먼저 JWT 인증을 처리
                 * → JWT가 유효하면 이미 인증 완료 상태로 이후 필터 통과
                 */
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * JwtAuthenticationFilter를 Bean으로 등록
     * @Bean으로 등록하지 않으면 Spring이 관리하지 않아
     * 내부의 의존성(@RequiredArgsConstructor)이 주입되지 않을 수 있음
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    /**
     * AuthenticationManager: 실제 인증을 수행하는 객체
     * AuthService.login()에서 이메일/비밀번호 검증 시 사용
     * AuthenticationConfiguration에서 자동 구성된 Manager를 가져옴
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * BCryptPasswordEncoder: 비밀번호 단방향 해시
     *
     * BCrypt 선택 이유:
     * - Salt 자동 생성 → 같은 비밀번호도 매번 다른 해시값 생성 (Rainbow table 공격 방어)
     * - Cost factor(기본값 10) → 해시 연산에 약 100ms 소요 → Brute force 공격 방어
     * - SHA-256 같은 빠른 해시는 비밀번호 저장에 부적합
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

