package z9.hobby.domain.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import z9.hobby.domain.favorite.entity.FavoriteEntity
import java.util.*

interface FavoriteRepository : JpaRepository<FavoriteEntity, Long> {

    @Query("SELECT f FROM FavoriteEntity f WHERE f.name IN :names")
    fun findByNameIn(@Param("names") names: List<String>): List<FavoriteEntity>

    @Query("SELECT f FROM FavoriteEntity f WHERE f.name = :name")
    fun findByName(@Param("name") name: String): Optional<FavoriteEntity>
}
