package org.study.budgetbook.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long accessExpiration;
    private final long refreshExpiration;


    /**
     * application.yml의 jwt 설정값을 생성자 주입으로 받음
     * //@Value: Spring이 yml 값을 필드에 직접 주입하는 어노테이션
     * <p>
     * 생성자 주입 이유:
     * - secretKey는 한 번 설정되면 변경되지 않아야 하는 불변값
     * - 필드 주입(@Autowired)은 테스트 시 목(Mock) 주입이 어려움
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        /**
         * 문자열 시크릿 → HMAC-SHA 키 객체로 변환
         *
         * HMAC-SHA256: 서버만 알고 있는 비밀키로 서명
         * → 클라이언트가 토큰을 위변조하면 서명 검증 실패
         * → 키 길이는 반드시 256비트(32바이트) 이상 필요
         */
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /*
    * accessToken
    * 만료 시간 : 15분 (application.properties에 jwt.access-expiration
    * */
    public String createAccessToken(Long userId, String role){
        return buildToken(userId, role, accessExpiration);
    }

    /*
    * refreshToken
    * 만료 일자 : 7일
    * AccessToken 재발급 전용으로만 사용
    * → 실제 API 호출에는 사용 불가 (SecurityConfig에서 별도 처리 필요)
    * */
    public String createRefreshToken(Long userId, String role){
        return buildToken(userId, role, refreshExpiration);
    }

    /**
     * JWT 토큰 생성 공통 메서드
     *
     * JWT 구조: Header.Payload.Signature
     * - Header: 알고리즘 정보 (HS256)
     * - Payload: subject(userId), role, iat(발급시간), exp(만료시간)
     * - Signature: Header + Payload를 secretKey로 서명한 값
     */
    private String buildToken(Long userId, String role, long expiration){
        Date date = new Date();
        return Jwts.builder()
                .subject(userId.toString()) // sub 플레임 : 토큰 주인 식별자
                .claim("role", role) // 커스텀 : 권한 식별자
                .issuedAt(date) // 발급시간
                .expiration(new Date(date.getTime() + expiration))  // 유효기간
                .signWith(secretKey) // 서명(위변조방지)
                .compact(); // 토큰 문자열 생성
    }

    /*
    * 토큰에서 userid 추출
    * subject에 userid저장 했으니 파싱 후 Long으로 변환
    * */
    public Long getUserId(String token){
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * 토큰에서 role 추출
     * buildToken()에서 "role" 키로 저장한 클레임 값을 가져옴
     */
    public String getRole(String token){
        return getClaims(token).get("role").toString();
    }

    public boolean validate(String token){
        try{
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
        }
        return false;
    }

    /*
    * 토큰 파싱 -> payload(Claims) 반환
    *
    * verifyWith(secretKey): 서명 검증에 사용할 키 지정
    * parseSignedClaims(): 서명된 JWT만 파싱 (unsigned JWT는 거부)
    * getPayload(): 검증 완료된 클레임 객체 반환
    * */
    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
