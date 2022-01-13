package com.zkteco.silkiddemo.Presenter;

import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.DepartmentStatus;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.CompanyModel;
import com.zkteco.silkiddemo.model.DepartmentModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.DepartmentView;
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

public class DepartmentPresenter {

    private DepartmentView mViewInterface;
    private APIClient mApiClient;

    public DepartmentPresenter(DepartmentView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void departmentapi(
            int company_id) {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("company_id", company_id);
      


        mApiClient.getAPI()
                .getDepartments(map,jsonObject)
                .enqueue(new Callback<DepartmentModel>() {
                    @Override
                    public void onResponse(Call<DepartmentModel> call, Response<DepartmentModel> response) {

                        DebugLog.e(String.valueOf(response.code()));



                        if (response.isSuccessful()) {
                            DepartmentModel departmentModel = response.body();
                            if(departmentModel != null){
                                mViewInterface.onSuccess(departmentModel);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<DepartmentModel> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", DepartmentStatus.DEPARTMENT_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(), DepartmentStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception", DepartmentStatus.SERVER_ERROR.getCode());
                        } else {
                            mViewInterface.onError("Unknown exception", DepartmentStatus.DEPARTMENT_STATUS_ERROR.getCode());
                        }
                    }
                });
    }


    private void getErrorMessage(int code, ResponseBody responseBody){
        ErrorCode errorCode = ErrorCode.getByCode(code);

        if (errorCode != null) {
            switch (errorCode) {
                case ERRORCODE500:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), DepartmentStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), DepartmentStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody), DepartmentStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), DepartmentStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), DepartmentStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
