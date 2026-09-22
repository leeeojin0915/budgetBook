package org.study.budgetbook.interfaces.api.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken; // API 호출시 Authorization 헤더에 사용
    private String refreshToken; // AccessToken 만료 시 재발급 요청에 사용
}
