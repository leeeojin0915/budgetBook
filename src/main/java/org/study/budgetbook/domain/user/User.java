package org.study.budgetbook.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.study.budgetbook.domain.common.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자. 직접 외부 생성 불가
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private Role role;
    private boolean deletedYn = false;


    public enum Role{
        ROLE_USER,
        ROLE_ADMIN
    }

    @Builder
    public User(String email, String password, String nickname, Role role){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    /*
    * 도메인 메서드
    * */

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void delete(){
        this.deletedYn = true;
    }
}
