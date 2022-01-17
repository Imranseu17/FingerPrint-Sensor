package com.zkteco.silkiddemo.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.core.utils.ToolUtils;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintCaptureListener;
import com.zkteco.android.biometric.module.fingerprintreader.FingerprintSensor;
import com.zkteco.android.biometric.module.fingerprintreader.FingprintFactory;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.android.biometric.module.fingerprintreader.exception.FingerprintException;
import com.zkteco.silkiddemo.Presenter.AttendencePresenter;
import com.zkteco.silkiddemo.Presenter.CompanyPresenter;
import com.zkteco.silkiddemo.Presenter.DataPresenter;
import com.zkteco.silkiddemo.Presenter.DepartmentPresenter;
import com.zkteco.silkiddemo.Presenter.EmployeePresenter;
import com.zkteco.silkiddemo.Presenter.InsertPresenter;
import com.zkteco.silkiddemo.Presenter.TestPresenter;
import com.zkteco.silkiddemo.R;
import com.zkteco.silkiddemo.Utils.DebugLog;
import com.zkteco.silkiddemo.databinding.ActivityMainBinding;
import com.zkteco.silkiddemo.model.AttendenceModel;
import com.zkteco.silkiddemo.model.CompanyModel;
import com.zkteco.silkiddemo.model.CompanyMessage;
import com.zkteco.silkiddemo.model.DataModel;
import com.zkteco.silkiddemo.model.DepartmentMessage;
import com.zkteco.silkiddemo.model.DepartmentModel;
import com.zkteco.silkiddemo.model.EmployeeMessage;
import com.zkteco.silkiddemo.model.EmployeeModel;
import com.zkteco.silkiddemo.model.InsertModel;
import com.zkteco.silkiddemo.service.VerificationService;
import com.zkteco.silkiddemo.view.AttendenceView;
import com.zkteco.silkiddemo.view.CompanyView;
import com.zkteco.silkiddemo.view.DataView;
import com.zkteco.silkiddemo.view.DepartmentView;
import com.zkteco.silkiddemo.view.EmployeeView;
import com.zkteco.silkiddemo.view.InsertView;
import com.zkteco.silkiddemo.view.TestView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements
         CompanyView , DepartmentView
         , EmployeeView {


    ActivityMainBinding mainBinding;
    CompanyPresenter companyPresenter;
    DepartmentPresenter departmentPresenter;
    EmployeePresenter employeePresenter;
    AppCompatButton goNext;
    MaterialSpinner companySpinner,departmentSpinner,dataSpinner;
    int empID;
    String empName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainBinding = DataBindingUtil. setContentView(this, R.layout.activity_main);

        companySpinner =  findViewById(R.id.companySpinner);
        departmentSpinner =  findViewById(R.id.departmentSpinner);
        dataSpinner =  findViewById(R.id.dataSpinner);
        goNext = findViewById(R.id.goNext);

        companyPresenter = new CompanyPresenter(this);
        departmentPresenter = new DepartmentPresenter(this);
        employeePresenter = new EmployeePresenter(this);

        if(checkConnection())
            companyPresenter.getCompanyCompany();

      goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FingerPrintActivity.class);
                intent.putExtra("empID",empID);
                intent.putExtra("empName",empName);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onSuccess(CompanyModel companyModel) {
        ArrayList<String> companyList = new ArrayList<>();
        String companyName;
        for (CompanyMessage companyMessage : companyModel.getMessage()){
            companyName = companyMessage.getName();
            companyList.add(companyName);
        }
        companySpinner.setItems(companyList);
        try {
            if(checkConnection())
                departmentPresenter.departmentapi(
                        Integer.parseInt(companyModel.getMessage().get(companySpinner.getSelectedIndex()).getId()));
        }catch (Exception e){
            e.printStackTrace();
        }

        companySpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(checkConnection())
                    try {
                        departmentPresenter.departmentapi(
                              Integer.parseInt(companyModel.getMessage().get(position).getId()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }
        });

    }

    @Override
    public void onSuccess(DepartmentModel departmentModel) {
        ArrayList<String> departmentList = new ArrayList<>();
        String departmentName;
        for (DepartmentMessage departmentMessage : departmentModel.getDepartmentMessages()){
            departmentName = departmentMessage.getDep_name();
            departmentList.add(departmentName);
        }
        departmentSpinner.setItems(departmentList);
        try {
            if(checkConnection())
                employeePresenter.employeeAPI(
                        Integer.parseInt(departmentModel.getDepartmentMessages().get(departmentSpinner.getSelectedIndex()).getId()));
        }catch (Exception e){
            e.printStackTrace();
        }
        departmentSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(checkConnection()){

                    try {
                        employeePresenter.employeeAPI(Integer.parseInt(
                                departmentModel.getDepartmentMessages().get(position).getId()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }
            }
        });
    }

    @Override
    public void onSuccess(EmployeeModel employeeModel) {
        ArrayList<String> employeeList = new ArrayList<>();
        String employeeInfo = null;
        for (EmployeeMessage employeeMessage:employeeModel.employeeMessageArrayList){

                employeeInfo = employeeMessage.getName()+" (#ID: "+employeeMessage.getId()+")";
                employeeList.add(employeeInfo);


        }
        dataSpinner.setItems(employeeList);
        if(employeeList != null){

            try {
                if(employeeModel.employeeMessageArrayList != null && dataSpinner.getItems() != null){
                    empID = Integer.parseInt(employeeModel.getEmployeeMessageArrayList()
                            .get(dataSpinner.getSelectedIndex()).getId());
                    empName = employeeModel.getEmployeeMessageArrayList().
                            get(dataSpinner.getSelectedIndex()).getName();

            }
            else {
                Toast.makeText(MainActivity.this,"No Employee Found in this role",Toast.LENGTH_LONG).show();
            }


            }
                catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }

            dataSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    try {
                        empID = Integer.parseInt(employeeModel.getEmployeeMessageArrayList().get(position).getId()) ;
                        empName = employeeModel.getEmployeeMessageArrayList().get(position).getName();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }else {
            Toast.makeText(MainActivity.this,"Employee List not Found",Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onError(String error, int code) {
        Toast.makeText(MainActivity.this,error,Toast.LENGTH_LONG).show();
    }


    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
