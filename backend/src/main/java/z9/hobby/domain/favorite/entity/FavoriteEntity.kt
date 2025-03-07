package z9.hobby.domain.favorite.entity;

import jakarta.persistence.*

@Entity
@Table(name = "favorites")
class FavoriteEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    val id: Long? = null,

    @Column(name = "favorite_name", nullable = false)
    var name: String
) {
    companion object {
        @JvmStatic
        fun createNewFavorite(name: String): FavoriteEntity {
            return FavoriteEntity(name = name)
        }
    }
}
