package com.zkteco.silkiddemo.Presenter;

import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.Utils.AttendenceStatus;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.Utils.IdentifyStatus;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.AttendenceModel;
import com.zkteco.silkiddemo.model.IdentifyModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.AttendenceView;
import com.zkteco.silkiddemo.view.IdentifyView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class AttendencePresenter {
    private AttendenceView mViewInterface;
    private APIClient mApiClient;

    public AttendencePresenter(AttendenceView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void attendenceAPI(int empID , String deviceID , String ipAddress , String dateTime ) {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eID",empID);
        jsonObject.addProperty("eDeviceID",deviceID);
        jsonObject.addProperty("eIP",ipAddress);
        jsonObject.addProperty("eDateTime",dateTime);


        mApiClient.getAPI()
                .attendence(map,jsonObject)
                .enqueue(new Callback<AttendenceModel>() {
                    @Override
                    public void onResponse(Call<AttendenceModel> call, Response<AttendenceModel> response) {

                        DebugLog.e(String.valueOf(response.code()));

                        if (response.isSuccessful()) {
                            AttendenceModel attendenceModel = response.body();
                            if(attendenceModel != null){
                                mViewInterface.onSuccess(attendenceModel);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<AttendenceModel> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", AttendenceStatus.Attendence_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(),AttendenceStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception",AttendenceStatus.SERVER_ERROR.getCode());
                        } else {
                            mViewInterface.onError("Unknown exception: "+e.getMessage(),AttendenceStatus.Attendence_STATUS_ERROR.getCode());
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
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody),AttendenceStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody),AttendenceStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody),AttendenceStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody),AttendenceStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody),AttendenceStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
