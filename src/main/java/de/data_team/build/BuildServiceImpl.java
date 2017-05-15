package de.data_team.build;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import de.data_team.build.jpa.DataService;
import de.data_team.build.model.Build;
import de.data_team.build.model.BuildHistory;
import de.data_team.build.model.Job;
import lombok.extern.slf4j.Slf4j;

@Service("buildService")
@Slf4j
public class BuildServiceImpl implements BuildService {

    private final AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

    private final ConcurrentHashMap<Long, Build> builds = new ConcurrentHashMap<>();

    private final BlockingQueue<Job> buildRequests = new ArrayBlockingQueue<>(100);

    private List<Job> commandList = new ArrayList<>();

    private final BuildNumberSequence sequence = new BuildNumberSequence();

    @Autowired
    private DataService dataService;

    @PostConstruct
    public void init() {
        commandList = dataService.listAllJobs();
    }

    @Override
    public synchronized void runBuild(final String jobName) {
        runBuild(() -> findJob(jobName));
    }

    @Override
    public synchronized void runBuild(final Long jobId) {
        runBuild(() -> findJob(jobId));
    }

    private synchronized void runBuild(final Supplier<Job> findJob) {
        try {
            final Job job = findJob.get();
            if (hasActiveBuildTask(job.getId())) {
                buildRequests.add(job);
            } else {
                startBuild(job);
            }
        } catch (final IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private Job findJob(final String name) {
        return commandList.stream().filter(j -> j.getName().equals(name)).findFirst().orElseThrow(
                () -> new IllegalArgumentException("Es ist kein Build-Job mit dem Namen '" + name + "' vorhanden."));
    }

    private Job findJob(final Long id) {
        return commandList.stream().filter(j -> j.getId().equals(id)).findFirst().orElseThrow(
                () -> new IllegalArgumentException("Es ist kein Build-Job mit der ID '" + id + "' vorhanden."));
    }

    private void startBuild(final Job job) {
        final BuildTask task = new BuildTask(this);
        final Build build = new Build(job, sequence.getNext(), task);
        final Future<?> process = executor.submit(task);
        build.setProcess(process);
        build.setStarted(LocalDateTime.now());
        builds.put(build.getBuildNumber(), build);
    }

    private boolean hasActiveBuildTask(final Long jobId) {
        return builds.values().stream().anyMatch(b -> b.getJob().getId().equals(jobId));
    }

    @Override
    public void cancelBuild(final Long buildNumber) {
        if (builds.containsKey(buildNumber)) {
            builds.get(buildNumber).stop();
        }
    }

    @Override
    public synchronized void buildDone(final Build build, final boolean taskStopped) {
        builds.remove(build.getBuildNumber());
        saveBuild(build, taskStopped);
        if (!buildRequests.isEmpty()) {
            final Job job = buildRequests.poll();
            LOGGER.info("Starte Build für Job [{}] aus Warteschlange.", job.getName());
            startBuild(job);
        }
    }

    private void saveBuild(final Build build, final boolean taskStopped) {
        final BuildHistory history = BuildHistory.from(build);
        history.setFinished(LocalDateTime.now());
        history.setResult(taskStopped ? 1 : 0);
        dataService.save(history);
    }

    @Override
    public Map<Long, Build> listActiveBuilds() {
        return builds;
    }

    @Override
    public List<Job> listBuildRequests() {
        return Arrays.asList(buildRequests.toArray(new Job[0]));
    }

    @Override
    public List<BuildHistory> listBuildHistory() {
        return dataService.listAllBuildHistories();
    }

    @Override
    public BuildHistory getBuildHistory(final Long buildNumber) {
        return dataService.findBuildHistories(buildNumber);
    }

    @Override
    public List<Job> listJobs() {
        return commandList;
    }

    @Override
    public String getOutput(final Long buildNumber) {
        final Build build = builds.get(buildNumber);
        return build == null ? "finished" : build.getExecutionOutput().toString();
    }

}
