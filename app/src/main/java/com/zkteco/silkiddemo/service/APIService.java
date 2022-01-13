package com.zkteco.silkiddemo.service;


import com.google.gson.JsonObject;
import com.zkteco.silkiddemo.model.AttendenceModel;
import com.zkteco.silkiddemo.model.CompanyModel;
import com.zkteco.silkiddemo.model.DataModel;
import com.zkteco.silkiddemo.model.DepartmentModel;
import com.zkteco.silkiddemo.model.EmployeeModel;
import com.zkteco.silkiddemo.model.InsertModel;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APIService {
    @POST("insert")
    Call<InsertModel> post(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @POST("api.php")
    Call<ResponseBody> insert(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @GET("get")
    Call<DataModel> getData(@HeaderMap Map<String, String> headers);

    @GET("get_companies")
    Call<CompanyModel> getCompany(@HeaderMap Map<String, String> headers);

    @POST("get_departments")
    Call<DepartmentModel> getDepartments(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @POST("get_employees")
    Call<EmployeeModel> getEmployees(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @POST("apply")
    Call<AttendenceModel> attendence(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

}
