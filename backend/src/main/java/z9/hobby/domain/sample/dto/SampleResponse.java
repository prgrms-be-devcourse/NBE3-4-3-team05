package z9.hobby.domain.sample.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.model.sample.SampleEntity;

public class SampleResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SampleDataInfo {
        private final String fullName;

        public static SampleDataInfo from(SampleEntity sample) {
            String fullName = String.format("%s-%s", sample.getFirstName(), sample.getSecondName());
            return SampleDataInfo.builder().fullName(fullName).build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SampleDataList {
        private final String fullName;
        private final Long id;

        public static SampleDataList from(SampleEntity sample) {
            String fullName = String.format("%s-%s", sample.getFirstName(), sample.getSecondName());
            return SampleDataList.builder().id(sample.getId()).fullName(fullName).build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SavedSampleData {
        private final Long id;

        public static SavedSampleData from(Long id) {
            return SavedSampleData.builder().id(id).build();
        }
    }
}
