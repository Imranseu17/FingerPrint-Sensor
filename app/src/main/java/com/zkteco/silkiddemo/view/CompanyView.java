package com.zkteco.silkiddemo.view;

import com.zkteco.silkiddemo.model.CompanyModel;


public interface CompanyView {
    public void onSuccess(CompanyModel companyModel);
    public void onError(String error, int code);
}
