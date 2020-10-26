package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.exceptions.ConstraintViolationException;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class JobServiceTest {

    @Autowired
    TestEntityManager em;

    JobService jobService;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    ShiftRepository shiftRepository;

    @Before
    public void initialize() {
        jobService = new JobService(jobRepository, shiftRepository);
    }

    @Test
    public void testIfJobHasAtLeastOneShift() {
        UUID companyId = UUID.randomUUID();
        Job job = jobService.createJob(companyId, LocalDateTime.parse("2020-07-16T12:00:00"), LocalDateTime.parse("2020-07-20T20:00:00"));
        List<Shift> shifts = shiftRepository.findAllByJob_Id(job.getId());
        Assert.assertTrue(shifts.size() > 0);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testIfTalentIsNotEligibleToWorkInConsecutiveShifts() {
        Job job1 = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T12:00:00"), LocalDateTime.parse("2020-07-20T15:00:00"));
        Job job2 = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T18:00:00"), LocalDateTime.parse("2020-07-20T20:00:00"));
        List<Shift> shifts1 = jobService.getShifts(job1.getId());
        List<Shift> shifts2 = jobService.getShifts(job2.getId());
        UUID talentId = UUID.randomUUID();
        jobService.bookTalent(shifts1.get(0).getId(), talentId);
        jobService.bookTalent(shifts2.get(0).getId(), talentId);
    }

    @Test
    public void testIfTalentIsEligibleToWorkInConsecutiveShifts() {
        Job job1 = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T12:00:00"), LocalDateTime.parse("2020-07-20T15:00:00"));
        Job job2 = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T18:00:00"), LocalDateTime.parse("2020-07-21T20:00:00"));
        List<Shift> shifts1 = jobService.getShifts(job1.getId());
        List<Shift> shifts2 = jobService.getShifts(job2.getId());
        UUID talentId = UUID.randomUUID();
        jobService.bookTalent(shifts1.get(0).getId(), talentId);
        jobService.bookTalent(shifts2.get(1).getId(), talentId);
    }

    @Test
    public void testIfJobGetsCancelledAllShiftsGetsCancelled() {
        Job job = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T18:00:00"), LocalDateTime.parse("2020-07-24T20:00:00"));
        List<Shift> shifts = jobService.getShifts(job.getId());
        Assert.assertTrue(shifts.size() == 5);
        jobService.cancelJob(job.getId());
        Assert.assertTrue(jobService.getShifts(job.getId()).size() == 0);
    }

    @Test
    public void testIfSingleShiftOfJobCanBeCancelled() {
        Job job = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T18:00:00"), LocalDateTime.parse("2020-07-24T20:00:00"));
        List<Shift> shifts = jobService.getShifts(job.getId());
        jobService.cancelShiftByShiftId(shifts.get(1).getId());
        Assert.assertTrue(jobService.getShifts(job.getId()).size() == 5);
    }

    @Test
    public void testCancelShiftForSpecificTalentAndReplaceWithNewTalent() {
        Job job = jobService.createJob(UUID.randomUUID(), LocalDateTime.parse("2020-07-20T18:00:00"), LocalDateTime.parse("2020-07-24T20:00:00"));
        List<Shift> shifts = jobService.getShifts(job.getId());
        UUID talentId = UUID.randomUUID();
        jobService.bookTalent(shifts.get(0).getId(), talentId);
        jobService.bookTalent(shifts.get(2).getId(), talentId);
        shifts = jobService.getShifts(job.getId());
        Assert.assertTrue(shifts.stream().filter(shift -> shift.getTalentId() != null && shift.getTalentId().equals(talentId)).count() == 2);
        jobService.cancelShiftForTalentId(talentId);
        shifts = jobService.getShifts(job.getId());
        Assert.assertTrue(shifts.stream().filter(shift -> shift.getTalentId() != null && shift.getTalentId().equals(talentId)).count() == 0);
    }

}
