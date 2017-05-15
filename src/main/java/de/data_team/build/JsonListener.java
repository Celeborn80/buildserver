package de.data_team.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.data_team.build.model.TriggerEvent;

@RestController()
@RequestMapping("/listener")
public class JsonListener {

    @Autowired
    private BuildService buildService;

    @PostMapping("/trigger")
    public void trigger(@RequestBody final TriggerEvent data) {
        buildService.runBuild(data.getRepository());
    }

}
