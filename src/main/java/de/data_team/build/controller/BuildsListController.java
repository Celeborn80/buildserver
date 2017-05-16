package de.data_team.build.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import de.data_team.build.model.Build;
import de.data_team.build.model.BuildHistory;
import de.data_team.build.model.BuildsList;
import de.data_team.build.model.Job;
import de.data_team.build.service.BuildService;

@Controller
public class BuildsListController {

    @Autowired
    private BuildService buildService;

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/listBuilds")
    @SendTo("/topic/builds/list")
    public Map<Long, Build> listBuilds() {
        return buildService.listActiveBuilds();
    }

    @MessageMapping("/listBuildRequests")
    @SendTo("/topic/buildRequests/list")
    public List<Job> listBuildRequests() {
        return buildService.listBuildRequests();
    }

    @MessageMapping("/listBuildHistory")
    @SendTo("/topic/buildHistory/list")
    public List<BuildHistory> listBuildHistory() {
        return buildService.listBuildHistory();
    }

    public void triggerList(final List<Build> buildsList) {
        template.convertAndSend("/topic/builds/list", new BuildsList(buildsList));
    }

    public void triggerUpdated(final Build build) {
        template.convertAndSend("/topic/builds/updated", build);
    }

    public void triggerFinished(final Build build) {
        template.convertAndSend("/topic/builds/finished", build);
    }

    public void triggerRequest(final List<Job> requests) {
        template.convertAndSend("/topic/buildRequests/list", requests);
    }

    public void triggerHistory(final List<BuildHistory> histories) {
        template.convertAndSend("/topic/buildHistory/list", histories);
    }

}
