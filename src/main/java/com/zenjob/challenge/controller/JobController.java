package com.zenjob.challenge.controller;

import com.zenjob.challenge.constants.ErrorMessages;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.exceptions.ConstraintViolationException;
import com.zenjob.challenge.service.JobService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class consists of REST endpoints for Job Resource
 */
@RestController
@RequestMapping(path = "/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * This method is used to create a Job
     *
     * @param dto the request object
     * @return the RequestJobResponse
     * @throws ConstraintViolationException when constraints or objective are not met
     */
    @PostMapping
    public ResponseDto<RequestJobResponse> requestJob(@RequestBody @Valid RequestJobRequestDto dto) throws ConstraintViolationException {
        this.validateShiftTimings(dto.start.getHour(), dto.end.getHour());
        Job job = jobService.createJob(dto.companyId, dto.start, dto.end);
        return ResponseDto.<RequestJobResponse>builder()
                .data(RequestJobResponse.builder()
                        .jobId(job.getId())
                        .build())
                .build();
    }

    /**
     * This method is used to cancel a particular job
     *
     * @param jobId the unique id of job
     */
    @DeleteMapping(path = "/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelJob(@PathVariable("jobId") UUID jobId) {
        jobService.cancelJob(jobId);
    }

    /**
     * validateShiftTimings method is used to validate whether
     * shift timing range is at most 8 hours and min 2 hours because
     * no talent is allowed to work less than 2 hours and more than 8 hours
     *
     * @param shiftStartTime valid start time of the shift
     * @param shiftEndTime   valid end time of the shift
     * @throws ConstraintViolationException if shift range is Invalid
     */
    public void validateShiftTimings(Integer shiftStartTime, Integer shiftEndTime) throws ConstraintViolationException {
        int totalShiftTime = shiftEndTime - shiftStartTime;
        System.out.println(totalShiftTime);
        if (shiftStartTime > shiftEndTime || totalShiftTime > 8 || totalShiftTime < 2) {
            throw new ConstraintViolationException(ErrorMessages.SHIFT_TIMING_RANGE_INVALID);
        }
    }

    /**
     * This class is used to create request object
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class RequestJobRequestDto {

        /**
         * companyId is the unique id of the company who is creating job
         */
        @NotNull(message = ErrorMessages.COMPANY_ID_INVALID)
        private UUID companyId;

        /**
         * start is the start date and time of the job
         * format is startDateOfJob(ISO format) : startTimeOfShift (ISO Format)
         */
        @NotNull(message = ErrorMessages.SHIFT_START_DATE_TIME_INVALID)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @FutureOrPresent(message = ErrorMessages.SHIFT_START_DATE_TIME_INVALID)
        private LocalDateTime start;

        /**
         * end is the end date and time of the job
         * format is endDateOfJob(ISO format) : endTimeOfShift (ISO Format)
         * example :  If start = 2013-07-20T18:00:00.176362, end = "2013-07-22T20:00:00.196362"
         * then start date of job is 2013-07-20 and end date of job is 2013-07-22
         * and shift timings for each day will be between 18:00:00.176362 to 20:00:00.196362
         */
        @NotNull(message = ErrorMessages.SHIFT_END_DATE_TIME_INVALID)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @FutureOrPresent(message = ErrorMessages.SHIFT_END_DATE_TIME_INVALID)
        private LocalDateTime end;
    }

    /**
     * This class is used to wrap the response
     */
    @Builder
    @Data
    private static class RequestJobResponse {
        /**
         * jobId the unique id of the Job
         */
        UUID jobId;
    }
}
