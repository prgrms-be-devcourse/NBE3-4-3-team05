package z9.hobby.domain.favorite.service;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import z9.hobby.domain.favorite.dto.FavoriteResponse
import z9.hobby.domain.favorite.entity.FavoriteEntity
import z9.hobby.domain.favorite.repository.FavoriteRepository

@Service
class FavoriteService(
    private val favoriteRepository: FavoriteRepository
) {
    @Transactional(readOnly = true)
    fun findAll(): MutableList<FavoriteResponse.ResponseData> {
        val favoriteList: MutableList<FavoriteEntity> = favoriteRepository.findAll()

        return favoriteList.stream()
            .map(FavoriteResponse.ResponseData::from)
            .toList();
    }
}
