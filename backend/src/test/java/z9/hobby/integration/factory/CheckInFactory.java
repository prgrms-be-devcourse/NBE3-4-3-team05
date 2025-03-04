package z9.hobby.integration.factory;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import z9.hobby.model.checkIn.CheckInEntity;
import z9.hobby.model.checkIn.CheckInEntityRepository;
import z9.hobby.model.schedules.SchedulesEntity;
import z9.hobby.model.user.User;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckInFactory {

    private final CheckInEntityRepository checkInEntityRepository;
    private final EntityManager em;

    public List<CheckInEntity> saveAndCreateCheckInData(
            final int count, SchedulesEntity schedule, User user, List<Boolean> check) {
        if(count == 0) return List.of();
        if(count != check.size()) {
            log.error("스케줄 생성 실패. cause : count != check.size");
        }

        List<CheckInEntity> saveCheckInList = new ArrayList<>(count);

        for(int index=1; index<=count; index++) {
            CheckInEntity newCheckIn = CheckInEntity
                    .builder()
                    .schedules(schedule)
                    .userId(user.getId())
                    .checkIn(check.get(index-1))
                    .build();
            CheckInEntity saveCheckIn = checkInEntityRepository.save(newCheckIn);
            saveCheckInList.add(saveCheckIn);
        }

        em.flush();
        em.clear();

        return saveCheckInList;
    }
}
