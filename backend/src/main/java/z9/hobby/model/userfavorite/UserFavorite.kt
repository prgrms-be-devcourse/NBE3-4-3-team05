package z9.hobby.model.userfavorite

import jakarta.persistence.*
import z9.hobby.domain.favorite.entity.FavoriteEntity
import z9.hobby.model.user.User

@Entity
@Table(name = "users_favorite")
data class UserFavorite(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_favorite_id")
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_id")
    val favorite: FavoriteEntity
) {
    companion object {
        @JvmStatic
        fun createNewUserFavorite(user: User, favorite: FavoriteEntity): UserFavorite {
            return UserFavorite(user = user, favorite = favorite)
        }
    }
}