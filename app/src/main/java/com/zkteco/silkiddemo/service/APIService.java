package com.zkteco.silkiddemo.service;


import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.model.AttendenceModel;
import com.zkteco.silkiddemo.model.IdentifyModel;
import com.zkteco.silkiddemo.model.InsertModel;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APIService {
    @POST("insert")
    Call<InsertModel> post(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @POST("api.php")
    Call<ResponseBody> insert(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @POST("verify")
    Call<IdentifyModel> verify(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @POST("apply")
    Call<AttendenceModel> attendence(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

}
