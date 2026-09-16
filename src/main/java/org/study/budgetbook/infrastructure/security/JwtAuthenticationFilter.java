package org.study.budgetbook.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

        /*
        * 2. 토큰 검증
        * */
        if(token != null && jwtTokenProvider.validate(token)) {
            Long userId = jwtTokenProvider.getUserId(token);
            String role = jwtTokenProvider.getRole(token);



        }

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
