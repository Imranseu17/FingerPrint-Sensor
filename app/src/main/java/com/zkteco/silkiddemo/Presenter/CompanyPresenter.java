package com.zkteco.silkiddemo.Presenter;

import com.zkteco.silkiddemo.Utils.CompanyStatus;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.Utils.ErrorCode;
import com.zkteco.silkiddemo.error.APIErrors;
import com.zkteco.silkiddemo.model.CompanyModel;
import com.zkteco.silkiddemo.model.CompanyModel;
import com.zkteco.silkiddemo.service.APIClient;
import com.zkteco.silkiddemo.view.CompanyView;
import com.zkteco.silkiddemo.view.CompanyView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class CompanyPresenter {
    private CompanyView mViewInterface;
    private APIClient mApiClient;

    public CompanyPresenter(CompanyView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void getCompanyCompany() {

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");




        mApiClient.getAPI()
                .getCompany(map)
                .enqueue(new Callback<CompanyModel>() {
                    @Override
                    public void onResponse(Call<CompanyModel> call, Response<CompanyModel> response) {

                        DebugLog.e(String.valueOf(response.code()));

                        if (response.isSuccessful()) {
                            CompanyModel companyModel = response.body();
                            if(companyModel != null){
                                mViewInterface.onSuccess(companyModel);
                            }
                        } else getErrorMessage(response.code(), response.errorBody());


                    }

                    @Override
                    public void onFailure(Call<CompanyModel> call, Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            getErrorMessage(code, responseBody);

                        } else if (e instanceof SocketTimeoutException) {
                            mViewInterface.onError("Server connection error", CompanyStatus.Company_STATUS_ERROR.getCode());
                        } else if (e instanceof IOException) {
                            if (e.getMessage() != null)
                                mViewInterface.onError(e.getMessage(), CompanyStatus.SERVER_ERROR.getCode());
                            else
                                mViewInterface.onError("IO Exception", CompanyStatus.SERVER_ERROR.getCode());
                        } else {
                            mViewInterface.onError("Unknown exception: "+e.getMessage(), CompanyStatus.Company_STATUS_ERROR.getCode());
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
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), CompanyStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE400:
                    mViewInterface.onError(APIErrors.get500ErrorMessage(responseBody), CompanyStatus.ERROR_CODE_100.getCode());
                    break;
                case ERRORCODE406:
                    mViewInterface.onError(APIErrors.get406ErrorMessage(responseBody), CompanyStatus.ERROR_CODE_406.getCode());
                    break;

                case SERVER_ERROR_CODE:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), CompanyStatus.SERVER_ERROR.getCode());
                    break;

                default:
                    mViewInterface.onError(APIErrors.getErrorMessage(responseBody), CompanyStatus.ERROR_CODE_100.getCode());
            }


        } else {

            mViewInterface.onError("Error occurred Please try again", code);

        }


    }
}
