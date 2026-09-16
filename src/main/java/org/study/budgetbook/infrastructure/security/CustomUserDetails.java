package org.study.budgetbook.infrastructure.security;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.study.budgetbook.domain.user.User;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 인증 주체(Principal) 객체
 *
 * UserDetails 인터페이스를 구현해야 Spring Security가 인식함
 * User Entity를 직접 사용하지 않는 이유:
 * - Entity를 Security 계층에 노출하면 계층 간 결합도가 높아짐
 * - 불필요한 필드(balance 등)까지 Security 컨텍스트에 올라가는 것을 방지
 */
@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * User Entity → CustomUserDetails 변환
     *
     * SimpleGrantedAuthority: "ROLE_USER" 같은 문자열을 권한 객체로 래핑
     * Spring Security의 hasRole("USER") 검사 시 "ROLE_" 접두사 자동 처리
     */
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
