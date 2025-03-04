package z9.hobby.integration.factory;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.favorite.repository.FavoriteRepository;

@Component
@RequiredArgsConstructor
public final class FavoriteFactory {

    public static final String FAVORITE_NAME_PREFIX = "관심사";

    private final EntityManager em;
    private final FavoriteRepository favoriteRepository;

    public List<FavoriteEntity> saveAndCreateFavoriteData(final int count) {
        if(count == 0) return List.of();

        List<FavoriteEntity> saveFavoriteList = new ArrayList<>(count);

        for(int index = 1; index <= count; index++) {
            String favoriteName = String.format("%s%d", FAVORITE_NAME_PREFIX, index);
            FavoriteEntity newFavorite = FavoriteEntity.createNewFavorite(favoriteName);
            FavoriteEntity saveFavorite = favoriteRepository.save(newFavorite);
            saveFavoriteList.add(saveFavorite);
        }

        em.flush();
        em.clear();

        return saveFavoriteList;
    }
}
