package z9.hobby.domain.classes.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.model.user.User;

public class ClassResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ClassResponseData {
        private final Long id;
        private final String name;
        private final String favorite;
        private final String description;

        public static ClassResponseData from(ClassEntity classes) {
            return ClassResponseData
                    .builder()
                    .id(classes.getId())
                    .name(classes.getName())
                    .favorite(classes.getFavorite())
                    .description(classes.getDescription())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class EntryResponseData {
        private final String name;
        private final String favorite;
        private final String description;

        public static EntryResponseData from(ClassEntity classes) {
            return EntryResponseData.builder()
                    .name(classes.getName())
                    .favorite(classes.getFavorite())
                    .description(classes.getDescription())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JoinResponseData {
        private final Long id;
        private final String name;

        public static JoinResponseData from(ClassEntity classes) {
            return JoinResponseData
                    .builder()
                    .id(classes.getId())
                    .name(classes.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ClassUserListData {
        private final Long classId;
        private final String name;
        private final Long masterId;
        private final List<ClassUserInfo> userList;

        public static ClassUserListData from(ClassEntity classes, List<User> users) {
            return ClassUserListData.builder()
                    .classId(classes.getId())
                    .name(classes.getName())
                    .masterId(classes.getMasterId())
                    .userList(users.stream()
                            .map(ClassUserInfo::from)
                            .toList())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ClassUserInfo {
        private final Long userId;
        private final String nickName;

        public static ClassUserInfo from(User user) {
            return ClassUserInfo.builder()
                    .userId(user.getId())
                    .nickName(user.getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CheckMemberData {
        private final boolean isMember;

        public static CheckMemberData from(boolean isMember) {
            return CheckMemberData.builder()
                    .isMember(isMember)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CheckBlackListData {
        private final boolean isBlackListed;

        public static CheckBlackListData from(boolean isBlackListed) {
            return CheckBlackListData.builder()
                    .isBlackListed(isBlackListed)
                    .build();
        }
    }
}
