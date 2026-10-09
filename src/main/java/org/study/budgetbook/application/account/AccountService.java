package org.study.budgetbook.application.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.budgetbook.domain.account.Account;
import org.study.budgetbook.domain.account.AccountRepository;
import org.study.budgetbook.domain.user.User;
import org.study.budgetbook.domain.user.UserRepository;
import org.study.budgetbook.interfaces.api.account.dto.AccountRequest;
import org.study.budgetbook.interfaces.api.account.dto.AccountResponse;
import org.study.budgetbook.interfaces.api.account.dto.BalanceUpdateRequest;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /*
    * 계좌 등록
    * */
    @Transactional
    public AccountResponse create(Long userId, AccountRequest request){
        User user = findActiveUser(userId); // 사용자 확인

        Account account = Account.builder()
                .user(user)
                .name(request.getName())
                .balance(request.getBalance())
                .type(request.getType())
                .currency(request.getCurrency())
                .build();
        return AccountResponse.from(accountRepository.save(account));
    }

    /*
    * 사용자 계좌 목록 조회
    * */
    public List<AccountResponse> getAccountResponseList(Long userId) {
        return accountRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(AccountResponse::from) // 메서드 참조
                .toList();
    }

    /*
    * 사용자 계좌 단건 조회
    * */
    public AccountResponse getAccountResponse(Long userId, Long accountId) {
        Account account = findMyAccount(userId, accountId);
        return AccountResponse.from(account);
    }

    /*
    * 계좌 정보 수정 (이름, 타입만 변경 가능)
    * */
    @Transactional
    public AccountResponse update(Long userId, Long accountId, AccountRequest request){
        Account account = findMyAccount(userId, accountId);
        account.updateInfo(request.getName(), request.getType());
        return AccountResponse.from(account);
    }

    /*
    * 잔액 수동 조정
    *
    * */
    @Transactional
    public AccountResponse adjustBalance(Long userId, Long accountId, BalanceUpdateRequest request){
        Account account = findMyAccount(userId, accountId);
        account.adjustBalance(request.getAmount());
        return AccountResponse.from(account);
    }

    @Transactional
    public void delete(Long userId, Long accountId){
        Account account = findMyAccount(userId, accountId);
        account.delete();
    }

    private Account findMyAccount(Long userId, Long accountId) {
        return accountRepository.findByUserIdAndIdAndDeletedFalse(userId, accountId)
                .orElseThrow(()-> new IllegalArgumentException("계좌를 찾을 수 없습니다."));
    }


    private User findActiveUser(Long userId){
        return userRepository.findByIdAndDeletedYnFalse(userId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않은 사용자 입니다."));
    }
}
