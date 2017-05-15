package de.data_team.build.model;

import lombok.Data;

@Data
public class TriggerEvent {

    private String repository;

    private String changeName;

    private String changeType;

}
