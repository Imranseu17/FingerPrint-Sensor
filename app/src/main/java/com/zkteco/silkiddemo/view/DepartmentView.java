package com.zkteco.silkiddemo.view;


import com.zkteco.silkiddemo.model.DepartmentModel;

public interface DepartmentView {
    public void onSuccess(DepartmentModel departmentModel);
    public void onError(String error, int code);
}
