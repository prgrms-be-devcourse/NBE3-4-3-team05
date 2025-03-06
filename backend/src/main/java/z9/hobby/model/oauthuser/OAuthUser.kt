package z9.hobby.model.oauthuser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import z9.hobby.model.BaseEntity;
import z9.hobby.model.user.User;

@Getter
@Entity
@ToString
@Table(name = "users_oauth")
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends BaseEntity {

    @Id
    @Column(name = "users_oauth_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private OAuthProvider provider;

    @Column(name = "uid", unique = true)
    private String uid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static OAuthUser createNewOAuthUser(String uid, OAuthProvider provider, User user) {
        return OAuthUser
                .builder()
                .provider(provider)
                .uid(uid)
                .user(user)
                .build();
    }
}
