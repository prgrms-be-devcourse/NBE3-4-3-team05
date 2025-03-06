package z9.hobby.domain.favorite.dto;

import z9.hobby.domain.favorite.entity.FavoriteEntity

class FavoriteResponse {
    data class ResponseData(
        val id: Long?,
        val favoriteName: String
    ) {
        companion object {
            @JvmStatic
            fun from(favorite: FavoriteEntity): ResponseData {
                return ResponseData(
                    id = favorite.id,
                    favoriteName = favorite.name
                )
            }
        }
    }
}
