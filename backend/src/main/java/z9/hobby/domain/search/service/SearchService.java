package z9.hobby.domain.search.service;

import static z9.hobby.domain.search.SortBy.CREATED_DESC;
import static z9.hobby.domain.search.SortBy.FAVORITE;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.repository.ClassRepository;
import z9.hobby.domain.favorite.repository.FavoriteRepository;
import z9.hobby.domain.search.SortBy;
import z9.hobby.domain.search.dto.SearchResponseDto;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.userfavorite.UserFavoriteRepository;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ClassRepository classRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserFavoriteRepository userFavoriteRepository;

    @Transactional(readOnly = true)
    public List<SearchResponseDto> searchClasses(SortBy sortBy, Long userId) {
        // 로그인 상태에 따른 기본 정렬 설정
        if (sortBy == null) {
            sortBy = (userId != null) ? FAVORITE : CREATED_DESC;
        }

        List<ClassEntity> classes;
        try {
            switch (sortBy) {
                case FAVORITE -> {
                    if (userId != null) {
                        // 로그인: 사용자의 관심사와 일치하는 모임만 조회
                        List<String> userFavorites = userFavoriteRepository.findFavoriteNamesByUserId(userId);

                        classes = classRepository.findByUserFavorites(userFavorites);
                    } else {
                        // 비로그인: 전체 모임을 관심사별, 가나다순 정렬
                        classes = classRepository.findByFavorites();
                    }
                }
                case NAME_ASC -> classes = classRepository.findAllByOrderByName();
                case PARTICIPANT_DESC -> classes = classRepository.findByParticipantSort();
                case CREATED_ASC -> classes = classRepository.findAllByOrderByCreatedAtAsc();
                case CREATED_DESC -> classes = classRepository.findAllByOrderByCreatedAtDesc();
                default -> classes = classRepository.findAllByOrderByCreatedAtDesc();
            }

            return classes.stream()
                    .map(SearchResponseDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CLASS_READ_FAILED);
        }
    }
}
