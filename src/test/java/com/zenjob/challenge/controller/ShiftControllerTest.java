package com.zenjob.challenge.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
public class ShiftControllerTest {

    @InjectMocks
    ShiftController shiftController;

    @Mock
    JobService jobService;

    @InjectMocks
    GlobalExceptionHandler exceptionHandler;

    MockMvc mockMvc;

    @Before
    public void initializeTests() {
        mockMvc = MockMvcBuilders.standaloneSetup(shiftController)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    public void testIfJobIdIsInvalidForGettingShiftsThenResponse400() throws Exception {
        String jobId = "b92be66b-ad8e-444b-b2ef-5f26a4ec40bd---";
        mockMvc.perform(get("/shift/" + jobId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCancelShiftByInvalidShiftIdThenResponse400() throws Exception {
        String shiftId = "b92be66b-ad8e-444b-b2ef-5f26a4ec40bd---";
        mockMvc.perform(delete("/shift/" + shiftId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCancelShiftByInvalidTalentIdThenResponse400() throws Exception {
        String talentId = "b92be66b-ad8e-444b-b2ef-5f26a4ec40bd---";
        mockMvc.perform(patch("/shift/talent/" + talentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testBookTalentByInvalidShiftIdThenResponse400() throws Exception {
        String shiftId = "b92be66b-ad8e-444b-b2ef-5f26a4ec40bd---";
        String body = "{\n" +
                "    \"talent\" : \"c56a4180-65aa-42ec-a945-5fd21dec0538\"\n" +
                "}";
        mockMvc.perform(patch("/shift/" + shiftId + "/book").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

}
