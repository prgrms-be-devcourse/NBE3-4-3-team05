package z9.hobby.domain.favorite.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.favorite.dto.FavoriteResponse;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.favorite.repository.FavoriteRepository;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public List<FavoriteResponse.ResponseData> findAll() {
        List<FavoriteEntity> favoriteList = favoriteRepository.findAll();

        return favoriteList.stream()
                .map(FavoriteResponse.ResponseData::from)
                .toList();
    }
}
