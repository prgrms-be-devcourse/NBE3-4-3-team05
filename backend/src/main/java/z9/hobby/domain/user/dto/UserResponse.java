package z9.hobby.domain.user.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

public class UserResponse {

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserInfo {
        private final String nickname;
        private final String type;
        private final String role;
        private final String createdAt;
        private final List<String> favorite;

        public static UserInfo of(User user, List<String> favorite) {
            String formattedDate = user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return UserInfo
                    .builder()
                    .nickname(user.getNickname())
                    .type(user.getType().getValue())
                    .role(user.getRole().getValue())
                    .createdAt(formattedDate)
                    .favorite(favorite)
                    .build();
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserSchedule {
        private final List<ScheduleInfo> schedule;

        public static UserSchedule from(List<ScheduleInfo> schedule) {
            return UserSchedule.builder().schedule(schedule).build();
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScheduleInfo {
        private final Long classId;
        private final String meetingTime; //yyyy-MM-dd
        private final String meetingTitle;

        public static ScheduleInfo from(SchedulesEntity schedulesEntity) {
            return ScheduleInfo.builder()
                    .classId(schedulesEntity.getClasses().getId())
                    .meetingTime(schedulesEntity.getMeetingTime())
                    .meetingTitle(schedulesEntity.getMeetingTitle())
                    .build();
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UserClass {
        private final List<ClassInfo> classInfo;

        public static UserClass from(List<ClassInfo> classInfo) {
            return UserClass.builder().classInfo(classInfo).build();
        }
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ClassInfo {
        private final Long classId;
        private final String name;
        private final String description;
        private final String favorite;

        public static ClassInfo from(ClassEntity classEntity) {
            return ClassInfo
                    .builder()
                    .classId(classEntity.getId())
                    .name(classEntity.getName())
                    .description(classEntity.getDescription())
                    .favorite(classEntity.getFavorite())
                    .build();
        }
    }
}
