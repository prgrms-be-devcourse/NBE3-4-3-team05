package z9.hobby.integration.factory;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.domain.classes.repository.ClassRepository;
import z9.hobby.domain.favorite.entity.FavoriteEntity;
import z9.hobby.model.user.User;

@Component
@RequiredArgsConstructor
public final class ClassFactory {

    public static final String CLASS_NAME_PREFIX = "새로운 모임";
    public static final String CLASS_DESCRIPTION = "모임 설명 글 입니다!!";

    private final ClassRepository classRepository;
    private final EntityManager em;

    public List<ClassEntity> saveAndCreateClassData(final int count, User user, FavoriteEntity favorite) {
        if(count == 0) return List.of();

        List<ClassEntity> saveClassDataList = new ArrayList<>(count);

        for(int index=1; index<=count; index++) {
            String className = String.format("%s%d", CLASS_NAME_PREFIX, index);
            ClassEntity newClass = ClassEntity
                    .builder()
                    .name(className)
                    .favorite(favorite.getName())
                    .description(CLASS_DESCRIPTION)
                    .masterId(user.getId())
                    .build();
            newClass.addMember(user.getId());
            ClassEntity saveClassData = classRepository.save(newClass);
            saveClassDataList.add(saveClassData);
        }

        em.flush();
        em.clear();

        return saveClassDataList;
    }
}
