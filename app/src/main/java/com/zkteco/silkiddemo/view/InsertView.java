package com.zkteco.silkiddemo.view;


import com.zkteco.silkiddemo.model.InsertModel;



public interface InsertView {
    public void onSuccess(InsertModel insertModel);
    public void onError(String error, int code);

}
