package de.data_team.build.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.StopWatch;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.LogOutputStream;

import de.data_team.build.model.Build;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuildTask implements Runnable {

    private Build build;

    private final BuildService buildService;

    private StartedProcess startedProcess;

    private boolean taskStopped = false;

    public BuildTask(final BuildService buildService) {
        this.buildService = buildService;
    }

    @Override
    public void run() {
        final StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            final String message = MessageFormatter.format("start build [{}]", build).getMessage();
            LOGGER.info(message);
            build.getExecutionOutput().append(message);
            build.getExecutionOutput().append(System.lineSeparator());
            build.getExecutionOutput().append(System.lineSeparator());

            TimeUnit.SECONDS.sleep(2);
            if (!taskStopped) {
                startedProcess = new ProcessExecutor().command(build.getJob().getCommand())
                        .redirectOutput(new LogOutputStream() {
                            @Override
                            protected void processLine(final String line) {
                                build.getExecutionOutput().append(line);
                                build.getExecutionOutput().append(System.lineSeparator());
                                buildService.getBuildsListController().triggerUpdated(build);
                            }
                        }).start();
                while (startedProcess.getProcess().isAlive()) {
                }
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        } finally {
            stopWatch.stop();
            LOGGER.info("finished build [{}], [{}]", build, stopWatch.shortSummary());
            build.finish();
            buildService.buildDone(build, taskStopped);
        }
    }

    public void stop() {
        taskStopped = true;
        if (startedProcess != null) {
            startedProcess.getProcess().destroyForcibly();
        }
    }

    public void setBuild(final Build build) {
        this.build = build;
    }

}
