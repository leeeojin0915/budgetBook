package org.study.budgetbook.interfaces.api.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.budgetbook.application.user.UserService;
import org.study.budgetbook.interfaces.api.user.dto.UpdateNicknameRequest;
import org.study.budgetbook.interfaces.api.user.dto.UpdatePasswordRequest;
import org.study.budgetbook.interfaces.api.user.dto.UserResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    /*
     * 내 정보 조회
     * */
    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getMe(userId));
    }

    /*
     * 닉네임변경
     * */
    @PatchMapping("/update/name")
    public ResponseEntity<UserResponse> updateUserName(@AuthenticationPrincipal Long userId,
                                                       @Valid @RequestBody UpdateNicknameRequest request) {
        return ResponseEntity.ok(userService.updateNickname(userId, request));
    }

    /*
     * 비밀번호 변경
     * */
    @PatchMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal Long userId,
                                               @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    /*
     * 회원 탈퇴
     * */
    @DeleteMapping("/info")
    public ResponseEntity<Void> deleteInfo(@AuthenticationPrincipal Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
