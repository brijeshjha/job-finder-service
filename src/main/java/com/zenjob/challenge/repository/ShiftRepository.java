package com.zenjob.challenge.repository;

import com.zenjob.challenge.constants.DatabaseQueries;
import com.zenjob.challenge.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    List<Shift> findAllByJob_Id(UUID jobId);

    @Modifying(clearAutomatically = true)
    @Query(DatabaseQueries.CANCEL_ALL_SHIFTS_FOR_TALENT)
    void cancelAllShiftsForTalent_Id(UUID currentTalentId, UUID newTalentId);

    @Query(DatabaseQueries.FIND_SHIFTS_FOR_TALENT)
    List<Shift> findAllShiftForTalent(UUID talentId);

}
