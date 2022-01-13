package com.zkteco.silkiddemo.Presenter;

import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.EmployeeStatus;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.EmployeeModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.EmployeeView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class EmployeePresenter {
    private EmployeeView mViewInterface;
    private APIClient mApiClient;

    public EmployeePresenter(EmployeeView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void employeeAPI(
            int department_id) {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("department_id", department_id);



        mApiClient.getAPI()
                .getEmployees(map,jsonObject)
                .enqueue(new Callback<EmployeeModel>() {
                    @Override
                    public void onResponse(Call<EmployeeModel> call, Response<EmployeeModel> response) {

                        DebugLog.e(String.valueOf(response.code()));



                        if (response.isSuccessful()) {
                            EmployeeModel employeeModel = response.body();
                            if(employeeModel != null){
                                mViewInterface.onSuccess(employeeModel);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<EmployeeModel> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", EmployeeStatus.EMPLOYEE_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(), EmployeeStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception", EmployeeStatus.SERVER_ERROR.getCode());
                        } else {
                            mViewInterface.onError("Unknown exception", EmployeeStatus.EMPLOYEE_STATUS_ERROR.getCode());
                        }
                    }
                });
    }


    private void getErrorMessage(int code, ResponseBody responseBody){
        ErrorCode errorCode = ErrorCode.getByCode(code);

        if (errorCode != null) {
            switch (errorCode) {
                case ERRORCODE500:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), EmployeeStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), EmployeeStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody), EmployeeStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), EmployeeStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), EmployeeStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
