package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.exceptions.ConstraintViolationException;
import com.zenjob.challenge.service.JobService;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class consists of REST endpoints for Shift Resource
 */
@RestController
@RequestMapping(path = "/shift")
@RequiredArgsConstructor
public class ShiftController {

    private final JobService jobService;

    /**
     * This method is used to get all shifts of a job
     *
     * @param uuid the unique id of the job
     * @return the GetShiftsResponse
     */
    @GetMapping(path = "/{jobId}")
    public ResponseDto<GetShiftsResponse> getShifts(@PathVariable("jobId") UUID uuid) {
        List<ShiftResponse> shiftResponses = jobService.getShifts(uuid).stream()
                .map(shift -> ShiftResponse.builder()
                        .id(shift.getId())
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getStartTime())
                        .end(shift.getEndTime())
                        .build())
                .collect(Collectors.toList());
        return ResponseDto.<GetShiftsResponse>builder()
                .data(GetShiftsResponse.builder()
                        .shifts(shiftResponses)
                        .build())
                .build();
    }

    /**
     * This method is used to cancel shift by shift Id
     *
     * @param shiftId the unique id of the shift
     * @throws ConstraintViolationException when constraints or objective are not met
     */
    @DeleteMapping("/{shiftId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelShiftByShiftId(@PathVariable("shiftId") UUID shiftId) throws ConstraintViolationException {
        jobService.cancelShiftByShiftId(shiftId);
    }

    /**
     * This method is used to cancel shift by talent id
     *
     * @param talentId the unique id of the talent
     * @throws ConstraintViolationException when constraints or objective are not met
     */
    @PatchMapping("/talent/{talentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelShiftByTalentId(@PathVariable("talentId") UUID talentId) throws ConstraintViolationException {
        jobService.cancelShiftForTalentId(talentId);
    }

    /**
     * This method is used to book talent for the shift provided
     *
     * @param shiftId the unique id of the shift
     * @param dto     the request object
     * @throws ConstraintViolationException when constraints or objective are not met
     */
    @PatchMapping(path = "/{id}/book")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void bookTalent(@PathVariable("id") UUID shiftId, @RequestBody @Valid ShiftController.BookTalentRequestDto dto) throws ConstraintViolationException {
        jobService.bookTalent(shiftId, dto.talent);
    }

    /**
     * This class is used to create request object
     */
    @NoArgsConstructor
    @Data
    private static class BookTalentRequestDto {
        UUID talent;
    }

    @Builder
    @Data
    private static class GetShiftsResponse {
        List<ShiftResponse> shifts;
    }

    /**
     * This class is used to wrap the response
     */
    @Builder
    @Data
    private static class ShiftResponse {
        UUID id;
        UUID talentId;
        UUID jobId;
        Instant start;
        Instant end;
    }
}
