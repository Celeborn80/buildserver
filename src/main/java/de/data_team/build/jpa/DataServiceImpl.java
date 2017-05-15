package de.data_team.build.jpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.data_team.build.model.BuildHistory;
import de.data_team.build.model.Job;

@Service("dataService")
public class DataServiceImpl implements DataService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BuildHistoryRepository buildHistoryRepository;

    @Override
    public List<Job> listAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public BuildHistory save(final BuildHistory buildHistory) {
        return buildHistoryRepository.save(buildHistory);
    }

    @Override
    public List<BuildHistory> listAllBuildHistories() {
        return buildHistoryRepository.findAll();
    }

    @Override
    public BuildHistory findBuildHistories(final Long buildNumber) {
        return buildHistoryRepository.findByBuildNumber(buildNumber);
    }

}
