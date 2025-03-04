package z9.hobby.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import z9.hobby.model.BaseEntity;

@Entity
@Getter
@ToString
@Table(name = "users")
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true)
    private String loginId;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UserType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public UserRole getUserRole() {
        return this.role;
    }

    public String getPassword() {
        return this.password;
    }

    public Long getId() {
        return this.id;
    }

    public UserStatus getStatus() {
        return this.status;
    }

    public static User createNewUser(String loginId, String password, String nickname) {
        return User
                .builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .type(UserType.NORMAL)
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .build();
    }

    public static User createNewOAuthUser(String nickname, String hashCode) {
        return User
                .builder()
                .nickname(nickname + hashCode)
                .type(UserType.OAUTH)
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .build();
    }

    public static User createSecurityContextUser(Long userId, UserRole userRole) {
        return User
                .builder()
                .id(userId)
                .role(userRole)
                .build();
    }

    public static User resign(User user) {
        return User
                .builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .type(user.getType())
                .status(UserStatus.DELETE)
                .role(user.getRole())
                .build();
    }

    public static User patchUserInfo(User user, String nickname) {
        return User
                .builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .nickname(nickname)
                .type(user.getType())
                .status(user.getStatus())
                .role(user.getRole())
                .build();
    }
}
