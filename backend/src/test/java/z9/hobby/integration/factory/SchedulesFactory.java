package z9.hobby.integration.factory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import z9.hobby.domain.classes.entity.ClassEntity;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.schedules.SchedulesRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulesFactory {

    public static final String SCHEDULES_MEETING_TITLE_PREFIX = "정기모임";

    private final SchedulesRepository schedulesRepository;
    private final EntityManager em;

    public List<SchedulesEntity> saveAndCreateClassData(final int count, ClassEntity classEntity) {
        if(count == 0) return List.of();

        List<SchedulesEntity> saveScheduleList = new ArrayList<>(count);

        for(int index=1; index<=count; index++) {
            String meetingTitle = String.format("%s%d", SCHEDULES_MEETING_TITLE_PREFIX, index);
            String formattedTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            SchedulesEntity newSchedule = SchedulesEntity
                    .builder()
                    .classes(classEntity)
                    .meetingTime(formattedTime)
                    .meetingTitle(meetingTitle)
                    .build();
            SchedulesEntity saveSchedule = schedulesRepository.save(newSchedule);
            saveScheduleList.add(saveSchedule);
        }

        em.flush();
        em.clear();

        return saveScheduleList;
    }
}
