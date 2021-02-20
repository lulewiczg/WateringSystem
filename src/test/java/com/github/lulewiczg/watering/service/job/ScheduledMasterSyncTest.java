package com.github.lulewiczg.watering.service.job;

import com.github.lulewiczg.watering.TestUtils;
import com.github.lulewiczg.watering.service.ActionService;
import com.github.lulewiczg.watering.service.dto.*;
import com.github.lulewiczg.watering.state.AppState;
import com.github.lulewiczg.watering.state.SystemStatus;
import com.github.lulewiczg.watering.state.dto.MasterResponse;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test", "testSlave"})
class ScheduledMasterSyncTest {

    @Autowired
    private ScheduledMasterSync job;

    @MockBean
    private AppState state;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ActionService actionService;

    @Mock
    private MasterResponse response;

    @Value("${com.github.lulewiczg.watering.master.url}")
    private String url;

    @Value("${com.github.lulewiczg.watering.master.login}")
    private String login;

    @Value("${com.github.lulewiczg.watering.master.password}")
    private String password;

    private final ActionDefinitionDto actionDef = new ActionDefinitionDto("test", "desc",
            String.class, Object.class, null, "param desc", String.class);

    private final ActionResultDto<String> actionResult = new ActionResultDto<>("test", "result", LocalDateTime.now());

    private final ActionResultDto<String> jobResult = new ActionResultDto<>("test", null, LocalDateTime.now(), "error");
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class)
//    void testJob(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDefinitionDto jobDef = new JobDefinitionDto("test", true);
//        when(actionService.getActions()).thenReturn(List.of(actionDef));
//        when(actionService.getJobs()).thenReturn(List.of(jobDef));
//        job.setActionResults(List.of(actionResult));
//        job.setJobResults(List.of(jobResult));
//
//        String credentials = login + ":" + password;
//        String base64 = Base64.encodeBase64String(credentials.getBytes());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic " + base64);
//        HttpEntity<SlaveStateDto> entity = new HttpEntity<>(new SlaveStateDto(state, List.of(actionDef), List.of(jobDef), List.of(actionResult), List.of(jobResult)), headers);
//
//        when(restTemplate.postForEntity(url, entity, MasterResponse.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
//        ActionDto action = new ActionDto("name", "param");
//        ActionDto action2 = new ActionDto("name2", "param2");
//        JobDto jobDto = new JobDto("test");
//        ActionResultDto<?> incomingJobResult = new ActionResultDto<>("result1", null, LocalDateTime.now());
//        ActionResultDto<?> incomingActionResult = new ActionResultDto<>("result2", null, LocalDateTime.now());
//        ActionResultDto<?> incomingActionResult2 = new ActionResultDto<>("result3", null, LocalDateTime.now(), "error");
//        Mockito.<ActionResultDto<?>>when(actionService.runJob(jobDto)).thenReturn(incomingJobResult);
//        Mockito.<ActionResultDto<?>>when(actionService.runAction(action)).thenReturn(incomingActionResult);
//        Mockito.<ActionResultDto<?>>when(actionService.runAction(action2)).thenReturn(incomingActionResult2);
//
//        when(response.getActions()).thenReturn(List.of(action, action2));
//        when(response.getJobs()).thenReturn(List.of(jobDto));
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result);
//        assertEquals(List.of(incomingJobResult), job.getJobResults());
//        assertEquals(List.of(incomingActionResult, incomingActionResult2), job.getActionResults());
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class)
//    void testWithId(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDto jobDto = new JobDto("test", UUID.randomUUID().toString());
//        when(restTemplate.postForEntity(eq(url), any(), eq(MasterResponse.class))).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
//
//        ActionResultDto<Void> result = job.run(jobDto);
//
//        TestUtils.testActionResult(result, "Sync with master failed");
//        assertEquals(jobDto.getId(), result.getId());
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class)
//    void testJobNothingToDo(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDefinitionDto jobDef = new JobDefinitionDto("test", true);
//        when(actionService.getActions()).thenReturn(List.of(actionDef));
//        when(actionService.getJobs()).thenReturn(List.of(jobDef));
//        job.setActionResults(List.of(actionResult));
//        job.setJobResults(List.of(jobResult));
//
//        String credentials = login + ":" + password;
//        String base64 = Base64.encodeBase64String(credentials.getBytes());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic " + base64);
//        HttpEntity<SlaveStateDto> entity = new HttpEntity<>(new SlaveStateDto(state, List.of(actionDef), List.of(jobDef), List.of(actionResult), List.of(jobResult)), headers);
//
//        when(restTemplate.postForEntity(url, entity, MasterResponse.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
//        JobDto syncDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(syncDto);
//
//        TestUtils.testActionResult(result);
//        verify(actionService, never()).runJob(any());
//        verify(actionService, never()).runAction(any());
//        assertEquals(new ArrayList<>(), job.getJobResults());
//        assertEquals(new ArrayList<>(), job.getActionResults());
//    }
//
//    @ParameterizedTest
//    @EnumSource(value = SystemStatus.class)
//    void testJobFail(SystemStatus status) {
//        when(state.getState()).thenReturn(status);
//        JobDefinitionDto jobDef = new JobDefinitionDto("test", true);
//        when(actionService.getActions()).thenReturn(List.of(actionDef));
//        when(actionService.getJobs()).thenReturn(List.of(jobDef));
//
//        String credentials = login + ":" + password;
//        String base64 = Base64.encodeBase64String(credentials.getBytes());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic " + base64);
//        HttpEntity<SlaveStateDto> entity = new HttpEntity<>(new SlaveStateDto(state, List.of(actionDef), List.of(jobDef), null, null), headers);
//
//        when(restTemplate.postForEntity(url, entity, MasterResponse.class)).thenThrow(HttpClientErrorException.BadRequest.class);
//        JobDto syncDto = new JobDto("test");
//
//        ActionResultDto<Void> result = job.run(syncDto);
//
//        TestUtils.testActionResult(result, "Unknown error!");
//        verify(actionService, never()).runJob(any());
//        verify(actionService, never()).runAction(any());
//    }
}
