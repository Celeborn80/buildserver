package de.data_team.build.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "build_history")
@NoArgsConstructor
@Data
public class BuildHistory {

    public BuildHistory(final Job job, final Long buildNumber, final LocalDateTime started, final String output) {
        this.job = job;
        this.buildNumber = buildNumber;
        this.started = started;
        this.output = output;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "finished")
    private LocalDateTime finished;

    @Column(name = "result")
    private int result;

    @ManyToOne
    private Job job;

    @Column(name = "buildNumber")
    private Long buildNumber;

    @Column(name = "started")
    private LocalDateTime started;

    @Column(name = "output")
    @Lob
    private String output;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getStarted() {
        return started;
    }

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalDateTime getFinished() {
        return finished;
    }

    public static BuildHistory from(final Build build) {
        return new BuildHistory(build.getJob(), build.getBuildNumber(), build.getStarted(), build.getOutput());
    }

}
