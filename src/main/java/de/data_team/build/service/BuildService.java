package de.data_team.build.service;

import java.util.List;
import java.util.Map;

import de.data_team.build.controller.BuildsListController;
import de.data_team.build.model.Build;
import de.data_team.build.model.BuildHistory;
import de.data_team.build.model.Job;

public interface BuildService {

    void buildDone(Build build, boolean taskStopped);

    void cancelBuild(Long jobId);

    String getOutput(Long buildNumber);

    Map<Long, Build> listActiveBuilds();

    List<BuildHistory> listBuildHistory();

    List<Job> listBuildRequests();

    List<Job> listJobs();

    void runBuild(Long jobId);

    void runBuild(String jobName);

    BuildHistory getBuildHistory(Long buildNumber);

    BuildsListController getBuildsListController();

}
