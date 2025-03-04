package z9.hobby.domain.user.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.repository.ClassRepository;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.domain.favorite.repository.FavoriteRepository;
import z9.hobby.domain.user.dto.UserRequest;
import z9.hobby.domain.user.dto.UserResponse;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.schedules.SchedulesRepository;
import z9.hobby.model.user.User;
import z9.hobby.model.user.UserRepository;
import z9.hobby.model.userfavorite.UserFavorite;
import z9.hobby.model.userfavorite.UserFavoriteRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final FavoriteRepository favoriteRepository;
    private final SchedulesRepository schedulesRepository;
    private final ClassRepository classRepository;

    @Transactional(readOnly = true)
    @Override
    public UserResponse.UserInfo findUserInfo(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<String> favorites = userFavoriteRepository.findFavoriteNamesByUserId(userId);

        return UserResponse.UserInfo.of(findUser, favorites);
    }

    @Transactional
    @Override
    public void patchUserInfo(UserRequest.PatchUserInfo requestDto, Long userId) {
        // 1. 회원 정보 수정
        // - 현재 닉네임만 수정 가능
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User newUser = User.patchUserInfo(findUser, requestDto.getNickname());
        User savedUser = userRepository.save(newUser);

        // 2. favorite 가 등록되어있는건지 확인
        List<String> favorite = requestDto.getFavorite();
        List<FavoriteEntity> findFavorites = favoriteRepository.findByNameIn(favorite);
        if(findFavorites.size() != favorite.size()) {
            throw new CustomException(ErrorCode.NOT_EXIST_FAVORITE);
        }

        // 3. 회원 관심사 수정
        // - 해당 부분은 PUT 처럼, 전달 받은 걸로 전체 대체 진행 합니다.
        // - 즉, ["축구", "야구"] 두개를 가지고 있는 회원이 ["축구"] 하나만 받으면,
        //   그 회원의 관심사는 ["축구"] 만 가지고 있어야 함.
        userFavoriteRepository.deleteByUserId(userId);
        List<UserFavorite> userFavoriteList = new ArrayList<>();
        for (FavoriteEntity findFavorite : findFavorites) {
            UserFavorite newUserFavorite = UserFavorite.createNewUserFavorite(savedUser, findFavorite);
            userFavoriteList.add(newUserFavorite);
        }
        userFavoriteRepository.saveAll(userFavoriteList);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse.UserSchedule findUserSchedules(Long userId) {
        List<SchedulesEntity> findData = schedulesRepository.findUserSchedulesInfoByUserId(userId);

        List<UserResponse.ScheduleInfo> scheduleInfoList = findData.stream()
                .map(UserResponse.ScheduleInfo::from)
                .toList();

        return UserResponse.UserSchedule.from(scheduleInfoList);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse.UserClass findUserClasses(Long userId) {
        List<ClassEntity> findData = classRepository.findByUserId(userId);

        List<UserResponse.ClassInfo> classInfoList = findData.stream()
                .map(UserResponse.ClassInfo::from)
                .toList();

        return UserResponse.UserClass.from(classInfoList);
    }
}
