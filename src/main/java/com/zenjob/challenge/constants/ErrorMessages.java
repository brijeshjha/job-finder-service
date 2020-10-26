package com.zenjob.challenge.constants;

/**
 * This class contains the Application Error Messages
 */
public class ErrorMessages {

    public static final String COMPANY_ID_INVALID = "Company ID is Invalid";
    public static final String SHIFT_START_DATE_TIME_INVALID = "Start time of shift is Invalid";
    public static final String SHIFT_END_DATE_TIME_INVALID = "End time of shift is Invalid";
    public static final String SHIFT_TIMING_RANGE_INVALID = "Shift timings must be at most 8 hours and min 2 hours because talents are not allowed to work less than 2 hours shift";
    public static final String JOB_INVALID = "Job details are not provided";
    public static final String SHIFT_NOT_PRESENT = "Shift provided does not exist";
    public static final String TALENT_ALREADY_WORKING_FOR_PROVIDED_SHIFT = "Talent is already working for the provided shift";
    public static final String BREAK_BETWEEN_SHIFT_INVALID = "There has to be at least a 6 hours break between shifts for a Talent";
    public static final String JOB_ID_NOT_PRESENT = "Job Id does not exist";
    public static final String SHIFT_NOT_CANCELLABLE = "Jobs have to have at least one shift";
    public static final String NO_TALENT_WITH_SHIFT_PRESENT = "No Shifts found for the provided Talent Id";
}
