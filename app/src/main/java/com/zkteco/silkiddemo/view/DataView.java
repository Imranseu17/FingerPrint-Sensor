package com.zkteco.silkiddemo.view;

import com.zkteco.silkiddemo.model.DataModel;




public interface DataView {
    public void onSuccess(DataModel dataModel);
    public void onError(String error, int code);
}
