package com.zenjob.challenge.service;

import com.zenjob.challenge.constants.ErrorMessages;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exceptions.ConstraintViolationException;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Repository
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    private final ShiftRepository shiftRepository;

    /**
     * createJob method allows a company to create the job
     *
     * @param companyId      the id of the company who creates a job
     * @param shiftStartDate the start date and time of shift
     * @param shiftEndDate   the end date and Time of shift
     * @return the Job that is created
     */
    public Job createJob(UUID companyId, LocalDateTime shiftStartDate, LocalDateTime shiftEndDate) {

        Job job = Job.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .startTime(shiftStartDate.toInstant(ZoneOffset.UTC))
                .endTime(shiftEndDate.toInstant(ZoneOffset.UTC))
                .build();

        List<Shift> shifts = LongStream.range(0, ChronoUnit.DAYS.between(shiftStartDate, shiftEndDate) + 1)
                .mapToObj(idx -> shiftStartDate.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .id(UUID.randomUUID())
                        .job(job)
                        .startTime(date.toInstant(ZoneOffset.UTC))
                        .endTime(date.toLocalDate().atTime(shiftEndDate.getHour(), shiftEndDate.getMinute(), shiftEndDate.getSecond(), shiftEndDate.getNano()).toInstant(ZoneOffset.UTC))
                        .build())
                .collect(Collectors.toList());

        job.setShifts(shifts);
        return jobRepository.save(job);
    }


    /**
     * getShifts is used to get all shifts for a specified jobId
     *
     * @param jobId the unique id of the job created by company
     * @return List of Shifts for the specified job ID
     */
    public List<Shift> getShifts(UUID jobId) {
        return shiftRepository.findAllByJob_Id(jobId);
    }

    /**
     * The menthod allows company to book talent for a specified shift
     *
     * @param shiftId  the identifier of the shift for which the talent needs to be booked
     * @param talentId the identifier of the talent to be booked
     */
    public void bookTalent(UUID shiftId, UUID talentId) throws ConstraintViolationException {
        if (!shiftRepository.existsById(shiftId)) {
            throw new ConstraintViolationException(ErrorMessages.SHIFT_NOT_PRESENT);
        }
        Optional<Shift> requiredShift = shiftRepository.findById(shiftId);
        checkIfTalentIsEligibleToWorkInShift(requiredShift.get(), talentId);
        requiredShift.map(shift -> shiftRepository.save(shift.setTalentId(talentId)));
    }

    /**
     * checkIfTalentIsEligileToWorkInShift method is used to check
     * if the talent has at least a 6 hour break between shifts
     *
     * @param requiredShift the shift for which the company needs to book talent
     * @param talentId      the talent which needs to be booked
     */
    private void checkIfTalentIsEligibleToWorkInShift(Shift requiredShift, UUID talentId) throws ConstraintViolationException {
        if (talentId.equals(requiredShift.getTalentId())) {
            throw new ConstraintViolationException(ErrorMessages.TALENT_ALREADY_WORKING_FOR_PROVIDED_SHIFT);
        }
        List<Shift> allCurrentShiftsOfTheTalent = shiftRepository.findAllShiftForTalent(talentId);

        //since there has to be at least a 6 hours break between shifts
        //so 6 hours  = 3600 minutes is the filter condition to be checked
        if (allCurrentShiftsOfTheTalent.size() != 0 && allCurrentShiftsOfTheTalent.stream()
                .filter(
                        currentShift -> Duration.between(
                                currentShift.getEndTime(), requiredShift.getStartTime()).toMinutes() < 3600 &&
                                currentShift.getEndTime().truncatedTo(ChronoUnit.DAYS).equals(requiredShift.getStartTime().truncatedTo(ChronoUnit.DAYS))
                ).findAny().isPresent()) {
            throw new ConstraintViolationException(ErrorMessages.BREAK_BETWEEN_SHIFT_INVALID);
        }
    }

    /**
     * cancelJob method is used to cancel job and all related shifts for a job
     *
     * @param jobId the unique id of the job to be cancelled
     */
    public void cancelJob(UUID jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ConstraintViolationException(ErrorMessages.JOB_ID_NOT_PRESENT);
        }
        jobRepository.deleteById(jobId);
    }

    /**
     * cancelShiftByShiftId method is used to cancel shift by shift id
     * First the shift record corresponding to the given shift id is retrieved from table
     * the retrieved shift record consists of job id
     * with the help of job id check if there is at least one shift
     *
     * @param shiftId the shift to be cancelled
     */
    public void cancelShiftByShiftId(UUID shiftId) {
        if (!shiftRepository.existsById(shiftId)) {
            throw new ConstraintViolationException(ErrorMessages.SHIFT_NOT_PRESENT);
        }
        Shift shift = shiftRepository.findById(shiftId).get();
        List<Shift> shifts = shiftRepository.findAllByJob_Id(shift.getJob().getId());
        if (shifts.size() == 1) {
            throw new ConstraintViolationException(ErrorMessages.SHIFT_NOT_CANCELLABLE);
        }
        shiftRepository.deleteById(shiftId);
    }

    /**
     * This method is used to cancel all shift of a talent and hire
     * a new talent for the shift
     *
     * @param currentTalentId the talent id to be removed from shift
     */
    public void cancelShiftForTalentId(UUID currentTalentId) throws ConstraintViolationException {
        if (shiftRepository.findAllShiftForTalent(currentTalentId).size() == 0) {
            throw new ConstraintViolationException(ErrorMessages.NO_TALENT_WITH_SHIFT_PRESENT);
        }
        UUID newTalentId = UUID.randomUUID();
        shiftRepository.cancelAllShiftsForTalent_Id(currentTalentId, newTalentId);
    }
}
