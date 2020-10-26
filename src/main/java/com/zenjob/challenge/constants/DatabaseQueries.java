package com.zenjob.challenge.constants;

/**
 * This class consists of all database queries
 */
public class DatabaseQueries {

    public static final String CANCEL_ALL_SHIFTS_FOR_TALENT = "Update Shift set talentId = ?2 where talentId = ?1";
    public static final String FIND_SHIFTS_FOR_TALENT = "from Shift s where s.talentId = ?1";

}
