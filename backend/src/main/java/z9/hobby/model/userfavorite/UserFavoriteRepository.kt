package z9.hobby.model.userfavorite

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserFavoriteRepository : JpaRepository<UserFavorite, Long> {

    fun deleteByUserId(userId: Long)

    @Query("SELECT f.name FROM UserFavorite uf JOIN uf.favorite f WHERE uf.user.id = :userId")
    fun findFavoriteNamesByUserId(@Param("userId") userId: Long): List<String>
}
