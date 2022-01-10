package com.zkteco.silkiddemo.view;



import okhttp3.ResponseBody;

public interface TestView {
    public void onSuccess(ResponseBody responseBody);
    public void onError(String error, int code);
}
