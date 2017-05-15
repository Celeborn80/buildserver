package de.data_team.build.model;

import java.time.LocalDateTime;
import java.util.concurrent.Future;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.data_team.build.BuildTask;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = { "executionOutput", "output", "process", "task" })
public class Build {

    private Job job;

    private Long buildNumber;

    private LocalDateTime started;

    private String output;

    private final StringBuilder executionOutput = new StringBuilder();

    private Future<?> process;

    private final BuildTask task;

    public Build(final Job job, final Long buildNumber, final BuildTask task) {
        this.job = job;
        this.buildNumber = buildNumber;
        this.task = task;
        this.task.setBuild(this);
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getStarted() {
        return started;
    }

    public void finish() {
        setOutput(executionOutput.toString());
    }

    public void stop() {
        task.stop();
    }

}
