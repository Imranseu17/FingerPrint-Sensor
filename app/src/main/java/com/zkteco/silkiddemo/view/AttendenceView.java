package com.zkteco.silkiddemo.view;

import com.zkteco.silkiddemo.model.AttendenceModel;


public interface AttendenceView {
    public void onSuccess(AttendenceModel attendenceModel);
    public void onError(String error, int code);
}
