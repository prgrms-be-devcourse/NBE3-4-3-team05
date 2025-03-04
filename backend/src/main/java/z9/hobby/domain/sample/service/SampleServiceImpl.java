package z9.hobby.domain.sample.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import z9.hobby.domain.sample.dto.SampleRequest;
import z9.hobby.domain.sample.dto.SampleResponse;
import z9.hobby.global.exception.CustomException;
import z9.hobby.global.response.ErrorCode;
import z9.hobby.model.sample.SampleEntity;
import z9.hobby.model.sample.SampleRepository;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

    private final SampleRepository sampleRepository;

    @Transactional(readOnly = true)
    @Override
    public SampleResponse.SampleDataInfo findSampleById(Long id) {
        SampleEntity findData = sampleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_SAMPLE_DATA));

        return SampleResponse.SampleDataInfo.from(findData);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SampleResponse.SampleDataList> findAllSampleData() {
        List<SampleEntity> all = sampleRepository.findAll();
        return all.stream()
                .map(SampleResponse.SampleDataList::from)
                .toList();
    }

    @Transactional
    @Override
    public SampleResponse.SavedSampleData saveNewSampleData(SampleRequest.NewSampleData newSampleData) {
        SampleEntity savedData = sampleRepository.save(SampleRequest.NewSampleData.from(newSampleData));
        return SampleResponse.SavedSampleData.from(savedData.getId());
    }
}
