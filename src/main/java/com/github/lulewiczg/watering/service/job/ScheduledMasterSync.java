package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.config.SlaveConfig;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.ActionResultDto;
import com.github.lulewiczg.watering.service.dto.JobDto;
import com.github.lulewiczg.watering.service.dto.SlaveStateDto;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Job for scheduled water tanks filling.
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@ConditionalOnBean(SlaveConfig.class)
public class ScheduledMasterSync extends ScheduledJob {

    private static final String SYNC_ERROR = "Sync with master failed";

    private final AppState state;

    private final RestTemplate restTemplate;

    private final ActionService actionService;

    private final JobRunner runner;

    private List<ActionResultDto<?>> actionResults = new ArrayList<>();

    private List<ActionResultDto<?>> jobResults = new ArrayList<>();

    @Value("${com.github.lulewiczg.watering.master.url}")
    private String url;

    @Value("${com.github.lulewiczg.watering.master.login}")
    private String login;

    @Value("${com.github.lulewiczg.watering.master.password}")
    private String password;

    @Scheduled(cron = "${com.github.lulewiczg.watering.schedule.master.sync.cron}")
    void schedule() {
        schedule(runner);
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
    public void doJob(JobDto job) {
        log.debug("Starting sync");
        MasterResponse response = connect();
        if (response == null) {
            log.error(SYNC_ERROR);
            throw new IllegalStateException(SYNC_ERROR);
        }

        log.debug("Got commands: {}", response);
        response.getActions().forEach(i -> {
            log.info("Running action: {}", i);
            actionResults.add(actionService.runAction(i));
        });
        response.getJobs().forEach(i -> {
            log.info("Running action: {}", i);
            jobResults.add(actionService.runJob(i));
        });
        state.setLastSync(LocalDateTime.now());
        log.debug("Sync Finished!");
    }

    private MasterResponse connect() {
        String credentials = login + ":" + password;
        String base64 = Base64.encodeBase64String(credentials.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64);
        HttpEntity<SlaveStateDto> entity = new HttpEntity<>(
                new SlaveStateDto(state, actionService.getActions(), actionService.getJobs(), actionResults, jobResults),
                headers);

        ResponseEntity<MasterResponse> response;
        try {
            response = restTemplate.postForEntity(url, entity, MasterResponse.class);
        } catch (RestClientException e) {
            log.error(SYNC_ERROR, e);
            throw e;
        }
        actionResults = new ArrayList<>();
        jobResults = new ArrayList<>();

        return response.getBody();
    }

}
