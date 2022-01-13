package com.zkteco.silkiddemo.Presenter;

import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.Utils.DataStatus;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.DataModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.DataView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class DataPresenter {

    private DataView mViewInterface;
    private APIClient mApiClient;

    public DataPresenter(DataView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void getDataAPI() {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");




        mApiClient.getAPI()
                .getData(map)
                .enqueue(new Callback<DataModel>() {
                    @Override
                    public void onResponse(Call<DataModel> call, Response<DataModel> response) {

                        DebugLog.e(String.valueOf(response.code()));

                        if (response.isSuccessful()) {
                            DataModel dataModel = response.body();
                            if(dataModel != null){
                                mViewInterface.onSuccess(dataModel);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<DataModel> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", DataStatus.Data_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(), DataStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception", DataStatus.SERVER_ERROR.getCode());
                        } else {
//                            mViewInterface.onError("Unknown exception: "+e.getMessage(), DataStatus.Data_STATUS_ERROR.getCode());
//                            e.printStackTrace();
                        }
                    }
                });
    }


    private void getErrorMessage(int code, ResponseBody responseBody){
        ErrorCode errorCode = ErrorCode.getByCode(code);

        if (errorCode != null) {
            switch (errorCode) {
                case ERRORCODE500:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), DataStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), DataStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody), DataStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), DataStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), DataStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
