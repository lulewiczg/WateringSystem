package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.SlaveConfig;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Job for scheduled water tanks filling.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnBean(SlaveConfig.class)
public class ScheduledMasterSync extends ScheduledJob {

    private final AppState state;

    private final RestTemplate restTemplate;

    private final ActionService actionService;

    @Value("${com.github.lulewiczg.watering.master.url}")
    private String url;

    @Value("${com.github.lulewiczg.watering.master.login}")
    private String login;

    @Value("${com.github.lulewiczg.watering.master.password}")
    private String password;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.master.sync.cron}")
    void schedule() {
        run();
    }

    @Override
    public boolean canBeStarted() {
        return true;
    }

    @Override
    public String getName() {
        return "Master server synchronization";
    }

    @Override
    protected SystemStatus getJobStatus() {
        return SystemStatus.IDLE;
    }

    @Override
    protected SystemStatus getState() {
        return state.getState();
    }

    @Override
    protected void doJob() {
        String credentials = login + ":" + password;
        String base64 = Base64.encodeBase64String(credentials.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64);
        HttpEntity<SlaveStateDto> entity = new HttpEntity<>(new SlaveStateDto(state, actionService.getActions(), actionService.getJobs()), headers);

        ResponseEntity<MasterResponse> response;
        try {
            response = restTemplate.postForEntity(url, entity, MasterResponse.class);
        } catch (RestClientException e) {
            log.error("Sync with master failed", e);
            return;
        }
        MasterResponse command = response.getBody();
        if (command == null) {
            log.error("Sync with master failed");
            return;
        }

        log.debug("Got commands: {}", command);
        command.getActions().forEach(actionService::runAction);
        command.getJobs().forEach(actionService::runJob);
    }

    @Override
    protected void doJobRunning() {
        //Do nothing
    }

}
