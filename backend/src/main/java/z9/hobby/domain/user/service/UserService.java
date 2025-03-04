package z9.hobby.domain.user.service;

import z9.hobby.domain.user.dto.UserRequest;
import z9.hobby.domain.user.dto.UserResponse;

public interface UserService {

    UserResponse.UserInfo findUserInfo(Long userId);

    void patchUserInfo(UserRequest.PatchUserInfo requestDto, Long userId);

    UserResponse.UserSchedule findUserSchedules(Long userId);

    UserResponse.UserClass findUserClasses(Long userId);
}
