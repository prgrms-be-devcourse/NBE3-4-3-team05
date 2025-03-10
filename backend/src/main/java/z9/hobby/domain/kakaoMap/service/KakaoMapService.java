package z9.hobby.domain.kakaoMap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.kakaoMap.dto.KakaoMapDto;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.schedules.SchedulesRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoMapService {
    private final SchedulesRepository schedulesRepository;

    @Transactional(readOnly = true)
    public List<KakaoMapDto.SchedulesLocationData> getLatLngInfo(
            String filterType,
            double bottomLeftLat,
            double bottomLeftLng,
            double topRightLat,
            double topRightLng,
            Long userId
    ) {
        if (filterType == null) {
            filterType = (userId != null) ? "FAVORITE" : "ALL";
        }

        List<SchedulesEntity> locationData;

        if (filterType.equals("FAVORITE") && userId != null) {
            locationData = schedulesRepository.findFavoriteSchedulesByUserId(userId, bottomLeftLat, bottomLeftLng, topRightLat, topRightLng);
        } else {
            locationData = schedulesRepository.findByLatLng(bottomLeftLat, bottomLeftLng, topRightLat, topRightLng);
        }

        // 일정이 없는경우
        if (locationData.isEmpty()) {
            throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        return locationData.stream()
                .map(schedule -> KakaoMapDto.SchedulesLocationData.from(schedule.getClasses(), schedule))
                .collect(Collectors.toList());
    }
}