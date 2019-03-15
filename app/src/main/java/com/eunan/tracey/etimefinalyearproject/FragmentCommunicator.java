package com.eunan.tracey.etimefinalyearproject;

import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetModel;

import java.util.Map;

public interface FragmentCommunicator {
    public void passData(Map<String,TimeSheetModel> timeSheetModelMap);
}
