package com.zkteco.silkiddemo.Presenter;

import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.Utils.InsertStatus;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.InsertModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.InsertView;
import com.zkteco.silkiddemo.view.TestView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class TestPresenter {

    private TestView mViewInterface;
    private APIClient mApiClient;

    public TestPresenter(TestView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void insertApi(
                        String eBase64,String temp) {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eBase64", eBase64);
        jsonObject.addProperty("template",temp);


        mApiClient.getAPI()
                .insert(map,jsonObject)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        DebugLog.e(String.valueOf(response.code()));



                        if (response.isSuccessful()) {
                            ResponseBody responseBody = response.body();
                            if(response != null){
                                mViewInterface.onSuccess(responseBody);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", InsertStatus.INSERT_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(), InsertStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception", InsertStatus.SERVER_ERROR.getCode());
                        } else {
                            mViewInterface.onError("Unknown exception", InsertStatus.INSERT_STATUS_ERROR.getCode());
                        }
                    }
                });
    }


    private void getErrorMessage(int code, ResponseBody responseBody){
        ErrorCode errorCode = ErrorCode.getByCode(code);

        if (errorCode != null) {
            switch (errorCode) {
                case ERRORCODE500:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), InsertStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), InsertStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody), InsertStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), InsertStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), InsertStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
