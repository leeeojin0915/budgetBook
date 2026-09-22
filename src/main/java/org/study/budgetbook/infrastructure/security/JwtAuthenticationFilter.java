package org.study.budgetbook.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출
        String token = resolveToken(request);
        log.debug("token :{}", token);

        /*
        * 2. 토큰 검증 SecurityContext에 인증 정보 저장
        *
        * SecurityContext > 현재 요청의 인증 정보를 담는 저장소
        * -> 컨트롤러에서 @AutenticationalPricipal 또는
        * SecurityContextHolder.getContext().getAuthentication()으로 호출
        * */
        if(token != null && jwtTokenProvider.validate(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            String role = jwtTokenProvider.getRole(token);

            /*
            * UsernamePasswordAuthenticationToken > Spring Security 표준 인증 객체
            * - principal: userId (컨트롤러에서 꺼낼 값)
            * - credentials: null (JWT 방식에서는 비밀번호 불필요)
            * - authorities: 권한.목록
            *
            * 3번째 파라미터(authorities)를 넘기면 인증 완료 상태로 설정됨
            * -> isAuthenticated() == true
            * */
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );

            // 요청 IP, 세선 ID 등 부가 정보 추가 (Spring Security 표준 관례)
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            //SecurityContext에 인증정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 3. 다음 필터로 요청 전달
        // → 인가(Authorization)는 SecurityConfig의 authorizeHttpRequests에서 처리
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     *
     * 표준 형식: "Authorization: Bearer eyJhbGci..."
     * "Bearer " (7자) 이후의 문자열이 실제 토큰
     *
     * StringUtils.hasText(): null, 빈 문자열, 공백 모두 false 처리
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
