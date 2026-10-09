package org.study.budgetbook.interfaces.api.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.study.budgetbook.application.account.AccountService;
import org.study.budgetbook.interfaces.api.account.dto.AccountRequest;
import org.study.budgetbook.interfaces.api.account.dto.AccountResponse;
import org.study.budgetbook.interfaces.api.account.dto.BalanceUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    /*
    * 계좌 등록
    * */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AccountRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.create(userId, request));
    }

    /*
    * 내 계좌 리스트 조회
    * */
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccountList(
            @AuthenticationPrincipal Long userId){
        return ResponseEntity.ok(accountService.getAccountResponseList(userId));
    }

    /*
    * 단건 계좌 조회
    * */
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accountId){
        return ResponseEntity.ok(accountService.getAccountResponse(userId,accountId));
    }

    /*
    * 계좌 정보 수정
    * */
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accountId,
            @Valid @RequestBody AccountRequest request){
        return ResponseEntity.ok(accountService.update(userId,accountId,request));
    }

    /*
    * 잔액 수동 조정
    * */
    @PutMapping("/{accountId}/balance")
    public ResponseEntity<AccountResponse> adjustBalance(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accountId,
            @Valid @RequestBody BalanceUpdateRequest request){
        return ResponseEntity.ok(accountService.adjustBalance(userId,accountId,request));
    }

    /*
    * 계좌 삭제
    * */
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long accountId){
        accountService.delete(userId,accountId);
        return ResponseEntity.noContent().build();
    }

}
