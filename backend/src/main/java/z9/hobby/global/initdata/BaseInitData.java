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
import z9.hobby.model.sample.SampleEntity;
import z9.hobby.model.sample.SampleRepository;
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

    private final SampleRepository sampleRepository;
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
        List<SampleEntity> sampleData = createSampleData(10);
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

    private List<SampleEntity> createSampleData(final int count) {
        if (sampleRepository.count() != 0) {
            return sampleRepository.findAll();
        }
        if (count == 0) {
            return null;
        }

        List<SampleEntity> savedDataList = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String firstName = "김";
            String secondName = String.format("%s%d", "아무개", i);
            SampleEntity sample = SampleEntity.builder().firstName(firstName).secondName(secondName)
                    .age(i).build();
            savedDataList.add(sampleRepository.save(sample));
        }

        return savedDataList;
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
            Long masterId = users.get(i-1).getId();

            // favorites 리스트에서 순환하면서 관심사를 선택 (인덱스가 넘어가면 처음부터 다시)
            String favorite = favorites.get((i-1) % favorites.size()).getName();

            ClassEntity classEntity = ClassEntity.builder()
                    .name("테스트 모임" + i)
                    .favorite(favorite)
                    .description("테스트 모임" + i + "의 설명입니다.")
                    .masterId(masterId)
                    .build();

            ClassEntity savedClass = classRepository.save(classEntity);
            savedClassList.add(savedClass);

            // 모임장을 ClassUser로 추가
            ClassUserEntity classUser = ClassUserEntity.builder()
                    .classes(savedClass)
                    .userId(masterId)
                    .build();
            classUserRepository.save(classUser);
        }

        return savedClassList;
    }

    private void createScheduleData(List<ClassEntity> classes) {
        if (schedulesRepository.count() != 0) {
            return;
        }

        for (ClassEntity classEntity : classes) {
            // 각 클래스마다 3개의 일정 생성
            for (int i = 1; i <= 3; i++) {
                // 현재 시간으로부터 i주 후로 설정
                LocalDate futureTime = LocalDate.now().plusWeeks(i);
                String meetingTime = futureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                SchedulesEntity schedule = SchedulesEntity.builder()
                        .classes(classEntity)
                        .meetingTime(meetingTime)
                        .meetingTitle("모임 " + classEntity.getId() + "의 " + i + "번째 일정")
                        .build();
                schedulesRepository.save(schedule);
            }
        }
    }
}