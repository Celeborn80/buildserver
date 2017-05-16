package de.data_team.build.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.data_team.build.model.Build;
import de.data_team.build.model.BuildHistory;
import de.data_team.build.model.Job;
import de.data_team.build.service.BuildService;

@RestController()
@RequestMapping()
public class BuildRestController {

    @Autowired
    private BuildService buildService;

    @GetMapping("/builds/{buildNumber}/output")
    public Map<String, String> getOutput(@PathVariable final Long buildNumber) {
        final String result = buildService.getOutput(buildNumber);
        return Collections.singletonMap("output", result);
    }

    @GetMapping("/builds/jobs/list")
    public List<Job> listJobs() {
        final List<Job> result = buildService.listJobs();
        Collections.sort(result);
        return result;
    }

    @GetMapping("/builds/history/list")
    public List<BuildHistory> listBuildHistory() {
        return buildService.listBuildHistory();
    }

    @GetMapping("/builds/history/{buildNumber}")
    public BuildHistory getBuildHistory(@PathVariable final Long buildNumber) {
        return buildService.getBuildHistory(buildNumber);
    }

    @GetMapping("/builds/jobs/{jobId}/run")
    public Map<String, String> runBuild(@PathVariable final Long jobId) {
        buildService.runBuild(jobId);
        return Collections.singletonMap("data", "success");
    }

    @GetMapping("/builds/jobs/{jobId}/cancel")
    public Map<String, String> cancelBuild(@PathVariable final Long jobId) {
        buildService.cancelBuild(jobId);
        return Collections.singletonMap("data", "success");
    }

    @GetMapping("/builds/active/list")
    public List<Build> listActiveBuilds() {
        return new ArrayList<>(buildService.listActiveBuilds().values());
    }

    @GetMapping("/builds/requests/list")
    public List<Job> listRequests() {
        return buildService.listBuildRequests();
    }

}
