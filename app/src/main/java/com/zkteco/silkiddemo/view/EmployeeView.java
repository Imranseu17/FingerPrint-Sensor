package com.zkteco.silkiddemo.view;


import com.zkteco.silkiddemo.model.EmployeeModel;

public interface EmployeeView {
    public void onSuccess(EmployeeModel employeeModel);
    public void onError(String error, int code);
}
