package z9.hobby.global.initdata;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.entity.ClassUserEntity;
import z9.hobby.domain.classes.repository.ClassRepository;
import z9.hobby.domain.classes.repository.ClassUserRepository;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.favorite.repository.FavoriteRepository;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.schedules.SchedulesRepository;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRepository;
import z9.hobby.model.userfavorite.UserFavorite;
import z9.hobby.model.userfavorite.UserFavoriteRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class BaseInitData {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteRepository favoriteRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final ClassRepository classRepository;
    private final ClassUserRepository classUserRepository;
    private final SchedulesRepository schedulesRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    void init() {
        List<FavoriteEntity> saveFavoriteData = createFavoriteData(); // 먼저 관심사 생성
        List<User> savedUserData = createUserData(10);
        List<ClassEntity> savedClassData = createClassData(10, savedUserData);
        createScheduleData(savedClassData);
        List<UserFavorite> savedUserFavoriteData = createUserFavoriteData(savedUserData, saveFavoriteData);
    }

    private List<UserFavorite> createUserFavoriteData(
            List<User> savedUserData,
            List<FavoriteEntity> saveFavoriteData) {
        List<UserFavorite> savedUserFavoriteData = new ArrayList<>();

        for (User savedUser : savedUserData) {
            for (FavoriteEntity savedFavorite : saveFavoriteData) {
                UserFavorite newUserFavorite = UserFavorite.createNewUserFavorite(savedUser, savedFavorite);
                UserFavorite save = userFavoriteRepository.save(newUserFavorite);
                savedUserFavoriteData.add(save);
            }
        }

        return savedUserFavoriteData;
    }

    private List<User> createUserData(final int count) {
        if (userRepository.count() != 0) {
            return userRepository.findAll();
        }
        if (count == 0) {
            return null;
        }

        List<User> savedUserList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String loginId = String.format("%s%d@email.com", "test", i);
            String password = passwordEncoder.encode("!test1234");
            String nickname = String.format("%s%d", "test", i);
            savedUserList.add(
                    userRepository.save(
                            User.createNewUser(loginId, password, nickname)));
        }

        return savedUserList;
    }

    private List<FavoriteEntity> createFavoriteData() {
        if (favoriteRepository.count() != 0) {
            return favoriteRepository.findAll();
        }

        List<FavoriteEntity> savedFavoriteList = new ArrayList<>();

        FavoriteEntity favorite1 = favoriteRepository.save(FavoriteEntity.createNewFavorite("축구"));
        FavoriteEntity favorite2 = favoriteRepository.save(FavoriteEntity.createNewFavorite("영화"));
        FavoriteEntity favorite3 = favoriteRepository.save(FavoriteEntity.createNewFavorite("독서"));
        FavoriteEntity favorite4 = favoriteRepository.save(FavoriteEntity.createNewFavorite("그림"));
        FavoriteEntity favorite5 = favoriteRepository.save(FavoriteEntity.createNewFavorite("코딩"));
        FavoriteEntity favorite6 = favoriteRepository.save(FavoriteEntity.createNewFavorite("음악"));

        savedFavoriteList.addAll(List.of(favorite1, favorite2, favorite3, favorite4, favorite5, favorite6));

        return savedFavoriteList;
    }

    private List<ClassEntity> createClassData(final int count, final List<User> users) {
        if (classRepository.count() != 0) {
            return classRepository.findAll();
        }

        // 먼저 저장된 관심사 목록을 가져옵니다
        List<FavoriteEntity> favorites = favoriteRepository.findAll();
        List<ClassEntity> savedClassList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            // 각 클래스의 모임장을 users 리스트에서 순차적으로 설정
            Long masterId = users.get(i - 1).getId();

            // favorites 리스트에서 순환하면서 관심사를 선택 (인덱스가 넘어가면 처음부터 다시)
            String favorite = favorites.get((i - 1) % favorites.size()).getName();

            ClassEntity classEntity = new ClassEntity(
                    null,
                    "테스트 모임" + i,
                    favorite,
                    "테스트 모임" + i + "의 설명입니다.",
                    masterId
            );

            ClassEntity savedClass = classRepository.save(classEntity);
            savedClassList.add(savedClass);

            // 모임장을 ClassUser로 추가
            ClassUserEntity classUser = new ClassUserEntity(
                    null,
                    savedClass,
                    masterId
            );
            classUserRepository.save(classUser);
        }

        return savedClassList;
    }

    private void createScheduleData(List<ClassEntity> classes) {
        if (schedulesRepository.count() != 0) {
            return;
        }

        // 여러 지역의 좌표와 주소 정보 (서울, 부산, 인천, 대전, 광주, 대구 등)
        List<LocationInfo> locations = List.of(
                new LocationInfo("서울특별시 강남구", 37.4959854, 127.0664091),
                new LocationInfo("서울특별시 마포구", 37.5563989, 126.9160531),
                new LocationInfo("서울특별시 종로구", 37.5729503, 126.9793579),
                new LocationInfo("부산광역시 해운대구", 35.1631138, 129.1636853),
                new LocationInfo("부산광역시 부산진구", 35.1631138, 129.0535567),
                new LocationInfo("인천광역시 연수구", 37.4100207, 126.6788725),
                new LocationInfo("인천광역시 미추홀구", 37.4635133, 126.6518776),
                new LocationInfo("대전광역시 유성구", 36.3624468, 127.3561953),
                new LocationInfo("광주광역시 서구", 35.1595454, 126.8526012),
                new LocationInfo("대구광역시 수성구", 35.8576551, 128.6362609),
                new LocationInfo("울산광역시 남구", 35.5447791, 129.330359),
                new LocationInfo("경기도 성남시", 37.4386945, 127.1378657),
                new LocationInfo("경기도 수원시", 37.2636188, 127.0286009),
                new LocationInfo("강원도 춘천시", 37.8813153, 127.7299707),
                new LocationInfo("제주특별자치도 제주시", 33.499621, 126.5311884),
                // 추가된 좌표들 (같은 지역에 조금 더 가까운 위치로 추가)
                new LocationInfo("서울특별시 강남구 역삼동", 37.4923319, 127.0292881),
                new LocationInfo("서울특별시 강남구 삼성동", 37.5088019, 127.0609549),
                new LocationInfo("서울특별시 마포구 홍대", 37.5558406, 126.9073901),
                new LocationInfo("서울특별시 종로구 인사동", 37.5744951, 126.9836842),
                new LocationInfo("인천광역시 연수구 송도", 37.3829287, 126.6569645),
                new LocationInfo("대전광역시 유성구 궁동", 36.3687468, 127.3440347),
                new LocationInfo("부산광역시 해운대구 센텀시티", 35.1691389, 129.1300197),
                new LocationInfo("경기도 성남시 분당구", 37.3866719, 127.1208295)
        );

        // 도로명 배열
        String[] streets = {"테헤란로", "강남대로", "종로", "을지로", "충정로", "광화문로", "압구정로", "삼성로", "역삼로", "논현로",
                "청담로", "한강대로", "강변로", "양평로", "세종로", "중앙로", "경인로", "수영로", "송도로", "경부대로"};

        // 랜덤 객체 생성
        java.util.Random random = new java.util.Random();

        // 각 위치별 생성된 일정 개수를 추적하기 위한 맵
        java.util.Map<String, Integer> locationCounts = new java.util.HashMap<>();
        for (LocationInfo location : locations) {
            locationCounts.put(location.address, 0);
        }

        int locationsCount = locations.size();   // 전체 지역 수

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 각 클래스에 대해 일정 생성
        for (int classIndex = 0; classIndex < classes.size(); classIndex++) {
            ClassEntity classEntity = classes.get(classIndex);

            // 클래스마다 기본 3개의 일정 생성
            for (int i = 0; i <= 2; i++) {  // 0부터 시작하여 오늘(0), 1주 후(1), 2주 후(2)로 설정
                // i가 0이면 오늘, 그렇지 않으면 i주 후로 설정
                LocalDate meetingDate = today.plusWeeks(i);
                String meetingTime = meetingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // 각 주차별로 2~3개의 마커 추가 (한 위치 주변에 여러 마커가 생성됨)
                int additionalMarkers = random.nextInt(2) + 2; // 2~3개의 추가 마커

                for (int j = 0; j <= additionalMarkers; j++) {
                    // 균등하게 분배하기 위해 위치 선택 (기본 로직 유지하고 약간의 랜덤성 추가)
                    int locationIndex = (classIndex * 3 + i + j) % locationsCount;
                    LocationInfo location = locations.get(locationIndex);

                    // 선택된 위치에 대한 카운트 증가
                    locationCounts.put(location.address, locationCounts.getOrDefault(location.address, 0) + 1);

                    // 같은 지역 내에서 약간의 변화를 줌 (반경 약 500m 이내로 변화)
                    // j가 0일 때는 변화를 거의 주지 않고, j가 클수록 더 큰 변화를 줌
                    double latVariation = (random.nextDouble() - 0.5) * 0.008 * (j + 1); // 거리에 따라 변화량 증가
                    double lngVariation = (random.nextDouble() - 0.5) * 0.008 * (j + 1); // 거리에 따라 변화량 증가

                    // 도로명과 번호 랜덤 생성
                    String street = streets[random.nextInt(streets.length)];
                    int streetNumber = random.nextInt(100) + 1;

                    // 오늘 일정인 경우 시간도 포함시킴
                    String titlePrefix = i == 0 ? "오늘 " : "";
                    String markerSuffix = j == 0 ? "" : " (추가지점 " + j + ")";

                    SchedulesEntity schedule = SchedulesEntity.builder()
                            .classes(classEntity)
                            .meetingTime(meetingTime)
                            .meetingTitle(titlePrefix + "모임 " + classEntity.getId() + "의 " + (i + 1) + "번째 일정" + markerSuffix)
                            .meetingPlace(location.address + " " + street + " " + streetNumber + "길")
                            .lat(location.lat + latVariation)
                            .lng(location.lng + lngVariation)
                            .build();
                    schedulesRepository.save(schedule);
                }
            }
        }
    }

    // 위치 정보를 담는 내부 클래스 - public 필드 사용
    @lombok.AllArgsConstructor
    private static class LocationInfo {
        public final String address;
        public final double lat;
        public final double lng;
    }

}