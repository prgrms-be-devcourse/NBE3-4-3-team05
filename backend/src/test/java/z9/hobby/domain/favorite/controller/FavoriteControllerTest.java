package z9.hobby.domain.favorite.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.integration.SpringBootTestSupporter;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class FavoriteControllerTest extends SpringBootTestSupporter {

    @Test
    @DisplayName("관심사 목록 조회")
    void getFavorites() throws Exception {
        favoriteRepository.save(FavoriteEntity.createNewFavorite("영화"));
        favoriteRepository.save(FavoriteEntity.createNewFavorite("독서"));
        favoriteRepository.save(FavoriteEntity.createNewFavorite("그림"));
        favoriteRepository.save(FavoriteEntity.createNewFavorite("축구"));
        favoriteRepository.save(FavoriteEntity.createNewFavorite("코딩"));
        favoriteRepository.save(FavoriteEntity.createNewFavorite("음악"));

        ResultActions result = mockMvc.perform(get("/api/v1/favorites")).andDo(print());

        List<FavoriteEntity> favoriteList = favoriteRepository.findAll();

        result
                .andExpect(handler().handlerType(FavoriteController.class))
                .andExpect(handler().methodName("getFavorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("요청 응답 성공"))
                .andExpect(jsonPath("$.code").value(200))
        ;

        for (int i = 0; i < favoriteList.size(); i++) {
            FavoriteEntity favorite = favoriteList.get(i);

            result
                    .andExpect(jsonPath("$.data[%d].id".formatted(i)).value(favorite.getId()))
                    .andExpect(jsonPath("$.data[%d].favoriteName".formatted(i)).value(favorite.getName()))
            ;
        }
    }
}
