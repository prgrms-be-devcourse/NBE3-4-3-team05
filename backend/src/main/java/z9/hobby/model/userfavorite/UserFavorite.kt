package z9.hobby.model.userfavorite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.model.user.User;

@Getter
@Entity
@ToString
@Table(name = "users_favorite")
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFavorite {

    @Id
    @Column(name = "user_favorite_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_id")
    private FavoriteEntity favorite;

    public static UserFavorite createNewUserFavorite(User user, FavoriteEntity favorite) {
        return UserFavorite
                .builder()
                .user(user)
                .favorite(favorite)
                .build();
    }
}