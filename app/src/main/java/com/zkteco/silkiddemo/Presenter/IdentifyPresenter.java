package com.zkteco.silkiddemo.Presenter;

import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.Utils.IdentifyStatus;
import com.zkteco.silkiddemo.Utils.InsertStatus;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.IdentifyModel;
import com.zkteco.silkiddemo.model.InsertModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.IdentifyView;
import com.zkteco.silkiddemo.view.InsertView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class IdentifyPresenter {

    private IdentifyView mViewInterface;
    private APIClient mApiClient;

    public IdentifyPresenter(IdentifyView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void identifyAPI(String temp) {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("template",temp);


        mApiClient.getAPI()
                .verify(map,jsonObject)
                .enqueue(new Callback<IdentifyModel>() {
                    @Override
                    public void onResponse(Call<IdentifyModel> call, Response<IdentifyModel> response) {

                        DebugLog.e(String.valueOf(response.code()));

                        if (response.isSuccessful()) {
                            IdentifyModel identifyModel = response.body();
                            if(identifyModel != null){
                                mViewInterface.onSuccess(identifyModel);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<IdentifyModel> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", IdentifyStatus.IDENTIFY_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(),IdentifyStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception",IdentifyStatus.SERVER_ERROR.getCode());
                        } else {
                            mViewInterface.onError("Unknown exception: "+e.getMessage(),IdentifyStatus.IDENTIFY_STATUS_ERROR.getCode());
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void getErrorMessage(int code, ResponseBody responseBody){
        ErrorCode errorCode = ErrorCode.getByCode(code);

        if (errorCode != null) {
            switch (errorCode) {
                case ERRORCODE500:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody),IdentifyStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody),IdentifyStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody),IdentifyStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody),IdentifyStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody),IdentifyStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
