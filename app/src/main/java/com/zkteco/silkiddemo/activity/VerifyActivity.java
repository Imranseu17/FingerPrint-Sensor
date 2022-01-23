package com.zkteco.silkiddemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

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
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mapzen.speakerbox.Speakerbox;
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
import com.zkteco.silkiddemo.Presenter.DataPresenter;
import com.zkteco.silkiddemo.R;
import com.zkteco.silkiddemo.databinding.ActivityVerifyBinding;
import com.zkteco.silkiddemo.model.AttendenceModel;
import com.zkteco.silkiddemo.model.DataModel;
import com.zkteco.silkiddemo.model.Message;
import com.zkteco.silkiddemo.service.VerificationService;
import com.zkteco.silkiddemo.view.AttendenceView;
import com.zkteco.silkiddemo.view.DataView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class VerifyActivity extends AppCompatActivity  implements DataView, AttendenceView {

    private static final int VID = 6997;
    private static final int PID = 288;
    private TextView textView = null;
    private ImageView imageView = null;
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 1;
    private byte[][] regtemparray = new byte[3][2048];
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];

    private FingerprintSensor fingerprintSensor = null;


    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";

    ActivityVerifyBinding verifyBinding;
    int threshold = 55;

    String strBase64;

    DataPresenter dataPresenter;
    AttendencePresenter attendencePresenter;

    byte[] tmpBuffer;

    VerificationService verificationService;

    String name;


    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action))
            {
                synchronized (this)
                {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        LogHelper.i("have permission!");
                    }
                    else
                    {
                        LogHelper.e("not permission!");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyBinding = DataBindingUtil. setContentView(this, R.layout.activity_verify);

        textView = verifyBinding.textView;
        imageView = verifyBinding.imageView;

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        imageView.startAnimation(animFadeIn);

        Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        imageView.startAnimation(animFadeOut);

        dataPresenter = new DataPresenter(this);
        attendencePresenter = new AttendencePresenter(this);



        InitDevice();
        startFingerprintSensor();
        try {
            OnBnBegin();
        } catch (FingerprintException e) {
            e.printStackTrace();
        }


        verifyBinding.goEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(VerifyActivity.this, MainActivity.class));
            }
        });
    }

    private void startFingerprintSensor() {
        // Define output log level
        LogHelper.setLevel(Log.VERBOSE);
        // Start fingerprint sensor
        Map fingerprintParams = new HashMap();
        //set vid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_VID, VID);
        //set pid
        fingerprintParams.put(ParameterHelper.PARAM_KEY_PID, PID);
        fingerprintSensor = FingprintFactory.createFingerprintSensor(this, TransportType.USB,
                fingerprintParams);
    }


    public void saveBitmap(Bitmap bm) {
        File f = new File("/sdcard/fingerprint", "test.bmp");
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitDevice()
    {
        UsbManager musbManager = (UsbManager)this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        Context context = this.getApplicationContext();
        context.registerReceiver(mUsbReceiver, filter);

        for (UsbDevice device : musbManager.getDeviceList().values())
        {
            if (device.getVendorId() == VID && device.getProductId() == PID)
            {
                if (!musbManager.hasPermission(device))
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                    musbManager.requestPermission(device, pendingIntent);
                }
            }
        }
    }

    public void OnBnBegin() throws FingerprintException
    {
        try {
            if (bstart) return;
            fingerprintSensor.open(0);
            final FingerprintCaptureListener listener = new FingerprintCaptureListener() {
                @Override
                public void captureOK(final byte[] fpImage) {
                    final int width = fingerprintSensor.getImageWidth();
                    final int height = fingerprintSensor.getImageHeight();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null != fpImage)
                            {
                                ToolUtils.outputHexString(fpImage);
                                LogHelper.i("width=" + width + "\nHeight=" + height);
                                Bitmap bitmapFp = ToolUtils.renderCroppedGreyScaleBitmap(fpImage, width, height);
                                saveBitmap(bitmapFp);
                                imageView.setImageBitmap(bitmapFp);
                            }
                        }
                    });
                }
                @Override
                public void captureError(FingerprintException e) {
                    final FingerprintException exp = e;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogHelper.d("CaptureError  errno=" + exp.getErrorCode() +
                                    ",Internal error code: " + exp.getInternalErrorCode() +
                                    ",message=" + exp.getMessage());
                        }
                    });
                }
                @Override
                public void extractError(final int err)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Extract fail, errorcode:" + err);
                        }
                    });
                }

                @Override
                public void extractOK(final byte[] fpTemplate)
                {
                     tmpBuffer = fpTemplate;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isRegister) {
                                byte[] bufids = new byte[256];
                                int ret = ZKFingerService.identify(tmpBuffer, bufids, threshold, 1);
                                if (ret > 0)
                                {
                                    String strRes[] = new String(bufids).trim().split("\t");
                                    textView.setText("The finger already enroll by " + strRes[0] + ",cancel enroll");
                                    isRegister = false;
                                    enrollidx = 0;
                                    return;
                                }

                                if (enrollidx > 0 && ZKFingerService.verify(regtemparray[enrollidx-1], tmpBuffer) <= 0)
                                {
                                    textView.setText("Please press the same finger 3 times for the enrollment !");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                                enrollidx++;
                                if (enrollidx == 3) {
                                    byte[] regTemp = new byte[2048];
                                    if (0 < (ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp))) {
                                        ZKFingerService.save(regTemp, "test" + uid++);
                                        System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);
                                        //Base64 Template
                                        strBase64 = Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);
                                        textView.setText("Enroll success, uid:" + uid +
                                                "count:" + ZKFingerService.count());
                                    } else {
                                        textView.setText("Enroll fail");
                                    }
                                    isRegister = false;
                                } else {
                                    textView.setText("You need to press the " + (3 - enrollidx) + "time fingerprint");
                                }
                            } else {

                                if(checkConnection())
                                    dataPresenter.getDataAPI();

                            }
                        }
                    });
                }


            };
            fingerprintSensor.setFingerprintCaptureListener(0, listener);
            fingerprintSensor.startCapture(0);
            bstart = true;
            textView.setText("Start capture success !");
        }catch (FingerprintException e)
        {
            textView.setText("Please connect your mobile phone to ZKTeco Sensor with OTG Cable !");
        }
    }


    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onSuccess(DataModel dataModel) {
                if(dataModel.getMessages() != null){
                    verificationService = new VerificationService(dataModel.getMessages(), VerifyActivity.this);
                    verificationService.messageReceived(tmpBuffer);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String dateTime =  formatter.format(date);
                    if(verificationService.isVerified()){
                                if (checkConnection())
                                    name = verificationService.geteName();
                                attendencePresenter.attendenceAPI(verificationService.getEmpID(),
                                        Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),
                                        ipv4Address(), dateTime);
                    }else{
                        MotionToast.Companion.darkColorToast(VerifyActivity.this,"Failed",
                                "The Fingerprint is not registered ! Please Enroll this",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(VerifyActivity.this,R.font.helvetica_regular));
                        verifyBinding.pressFingerPrint.setVisibility(View.VISIBLE);
                        verifyBinding.entryText.setVisibility(View.VISIBLE);
                        verifyBinding.goEntry.setVisibility(View.VISIBLE);
                        verifyBinding.animation1.setVisibility(View.GONE);
                        verifyBinding.animation2.setVisibility(View.GONE);
                        verifyBinding.animation3.setVisibility(View.VISIBLE);
                        verifyBinding.loadingAnimationView.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                verifyBinding.animation3.setVisibility(View.GONE);
                                verifyBinding.animation1.setVisibility(View.VISIBLE);
                            }
                        }, 2000);

                        Speakerbox speakerbox = new Speakerbox(getApplication());
                        speakerbox.play("Invalid FingerPrint");
                    }
                }else {
                    MotionToast.Companion.darkColorToast(VerifyActivity.this,"Failed",
                            "No Enrollment Fingerprint Found",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(VerifyActivity.this,R.font.helvetica_regular));
                }
            }
    @Override
    public void onSuccess(AttendenceModel attendenceModel) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
            Date date = new Date();
            String dateTime =  formatter.format(date);
            verifyBinding.pressFingerPrint.setText("");
            verifyBinding.entryText.setText("");
            verifyBinding.goEntry.setVisibility(View.GONE);
            verifyBinding.animation1.setVisibility(View.GONE);
            verifyBinding.animation2.setVisibility(View.VISIBLE);
            verifyBinding.animation3.setVisibility(View.GONE);
            verifyBinding.loadingAnimationView.setVisibility(View.VISIBLE);
            Speakerbox speakerbox = new Speakerbox(getApplication());
            speakerbox.play("FingerPrint Success Please Wait .....");
        new Handler().postDelayed(new Runnable(){
    @Override
    public void run() {
            Intent intent = new Intent(VerifyActivity.this,WelcomeActivity.class);
            intent.putExtra("eName",name);
            intent.putExtra("dateTime",dateTime);
            startActivity(intent);
    }
        }, 3000);
          //  Toast.makeText(VerifyActivity.this,attendenceModel.getMessage(),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error, int code) {
        Toast.makeText(VerifyActivity.this,error,Toast.LENGTH_LONG).show();
    }

    private String ipv4Address(){

        Context context = this.getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return  ip;
    }


}