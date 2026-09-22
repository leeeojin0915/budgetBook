package org.study.budgetbook.interfaces.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import org.study.budgetbook.domain.user.User;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String role;
    private LocalDateTime createdAt;

    /**
     * Entity → DTO 변환 정적 팩토리 메서드
     *
     * Controller나 Service에서 new UserResponse(...)로 직접 생성하지 않고
     * UserResponse.from(user)로 통일하는 이유:
     * - 변환 로직이 한 곳에 집중 → 필드 추가/제거 시 수정 지점 최소화
     * - Entity를 외부(Controller)에 직접 노출하지 않음
     *   (Entity를 그대로 반환하면 양방향 연관관계에서 무한 순환 참조 발생 가능)
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .createdAt(user.getCreateAt())
                .build();
    }
}
