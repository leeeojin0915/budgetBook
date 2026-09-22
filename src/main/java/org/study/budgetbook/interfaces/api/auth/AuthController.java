package org.study.budgetbook.interfaces.api.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.study.budgetbook.application.auth.AuthService;
import org.study.budgetbook.interfaces.api.auth.dto.LoginRequest;
import org.study.budgetbook.interfaces.api.auth.dto.SignupRequest;
import org.study.budgetbook.interfaces.api.auth.dto.TokenResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * POST /api/auth/signup
     * 201 Created 반환: 리소스 생성 성공을 의미하는 HTTP 표준 상태코드
     * Body 없이 상태코드만 반환 (생성된 userId를 노출하지 않음)
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * POST /api/auth/login
     *
     * @Valid: SignupRequest의 @NotBlank, @Email 등 검증 어노테이션 활성화
     * 검증 실패 시 MethodArgumentNotValidException 발생 → 400 Bad Request
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestHeader("Authorization") String bearerToken) {
        String refreshToken = bearerToken.substring(7);// "Bearer " 제거
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
