package com.zkteco.silkiddemo.view;

import com.zkteco.silkiddemo.model.IdentifyModel;




public interface IdentifyView {
    public void onSuccess(IdentifyModel identifyModel);
    public void onError(String error, int code);
}
