package com.zenjob.challenge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.exceptions.GlobalExceptionHandler;
import com.zenjob.challenge.service.JobService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
public class JobControllerTest {

    @InjectMocks
    JobController jobcontroller;

    @Mock
    JobService jobService;

    @InjectMocks
    GlobalExceptionHandler exceptionHandler;

    MockMvc mockMvc;

    @Before
    public void initializeTests() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobcontroller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    public void testIfCompanyIdIsInvalidThenResponse400() throws Exception {
        String content = "{\n" +
                " \"companyId\" : \"\",\n" +
                " \"start\" : \"2020-09-22T14:10:00.176362\",\n" +
                " \"end\" : \"2020-09-22T17:00:00.196362\"\n" +
                "}";
        mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIfStartTimeIsInvalidThenResponse400() throws Exception {
        String content = "{\n" +
                " \"companyId\" : \"C56A4180-65AA-42EC-A945-5FD21DEC0538\",\n" +
                " \"start\" : \"\",\n" +
                " \"end\" : \"2020-09-22T17:00:00.196362\"\n" +
                "}";
        mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIfEndTimeIsInvalidThenResponse400() throws Exception {
        String content = "{\n" +
                " \"companyId\" : \"C56A4180-65AA-42EC-A945-5FD21DEC0538\",\n" +
                " \"start\" : \"2020-09-22T14:10:00.176362\",\n" +
                " \"end\" : \"\"\n" +
                "}";
        mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIfShiftTimingsRangeIsInvalidThenResponse400() throws Exception {
        String content = "{\n" +
                " \"companyId\" : \"C56A4180-65AA-42EC-A945-5FD21DEC0538\",\n" +
                " \"start\" : \"2020-09-22T10:10:00.176362\",\n" +
                " \"end\" : \"2020-09-22T20:00:00.196362\"\n" +
                "}";
        mockMvc.perform(post("/job")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void testIfAllFieldsAreValidThenResponse200() throws Exception {
        String content = "{\n" +
                " \"companyId\" : \"C56A4180-65AA-42EC-A945-5FD21DEC0538\",\n" +
                " \"start\" : \"2020-09-22T15:10:00.176362\",\n" +
                " \"end\" : \"2020-09-22T20:00:00.196362\"\n" +
                "}";
        Job job = Job.builder().build();
        when(jobService.createJob(any(UUID.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(job);
        mockMvc.perform(post("/job")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isOk());
    }

    @Test
    public void testIfJobIdToBeCancelledIsInvalidThenReturn400() throws Exception {
        mockMvc.perform(delete("/job/123344")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testIfJobIdToBeCancelledIsValidThenReturn204() throws Exception {
        mockMvc.perform(delete("/job/" + UUID.randomUUID())
        ).andExpect(status().isNoContent());
    }

}
