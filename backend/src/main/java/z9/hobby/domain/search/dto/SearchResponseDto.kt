package z9.hobby.domain.search.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import z9.hobby.domain.classes.entity.ClassEntity;

@Getter
@Builder
public class SearchResponseDto {
    private Long id;
    private String name;
    private String favorite;
    private String description;
    private int participantCount;
    private Long masterId;
    private LocalDateTime createdAt;

    public static SearchResponseDto from(ClassEntity entity) {
        return SearchResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .favorite(entity.getFavorite())
                .description(entity.getDescription())
                .participantCount(entity.getUsers().size())
                .masterId(entity.getMasterId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
