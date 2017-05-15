package de.data_team.build.jpa;

import java.util.List;

import de.data_team.build.model.BuildHistory;
import de.data_team.build.model.Job;

public interface DataService {

    List<Job> listAllJobs();

    BuildHistory save(BuildHistory buildHistory);

    List<BuildHistory> listAllBuildHistories();

    BuildHistory findBuildHistories(Long buildNumber);

}
