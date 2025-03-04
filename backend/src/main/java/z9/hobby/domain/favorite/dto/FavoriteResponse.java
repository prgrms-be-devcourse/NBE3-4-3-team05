package z9.hobby.domain.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.domain.favorite.entity.FavoriteEntity;

public class FavoriteResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseData {
        private final Long id;
        private final String favoriteName;

        public static ResponseData from(FavoriteEntity favorite) {
            return ResponseData.builder()
                    .id(favorite.getId())
                    .favoriteName(favorite.getName())
                    .build();
        }
    }
}
