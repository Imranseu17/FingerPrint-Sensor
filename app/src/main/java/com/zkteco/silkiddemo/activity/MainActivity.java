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
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

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
import com.zkteco.silkiddemo.Presenter.IdentifyPresenter;
import com.zkteco.silkiddemo.Presenter.InsertPresenter;
import com.zkteco.silkiddemo.R;
import com.zkteco.silkiddemo.Utils.AttendenceStatus;
import com.zkteco.silkiddemo.databinding.ActivityMainBinding;
import com.zkteco.silkiddemo.model.AttendenceModel;
import com.zkteco.silkiddemo.model.IdentifyModel;
import com.zkteco.silkiddemo.model.InsertModel;
import com.zkteco.silkiddemo.view.AttendenceView;
import com.zkteco.silkiddemo.view.IdentifyView;
import com.zkteco.silkiddemo.view.InsertView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class MainActivity extends AppCompatActivity implements InsertView , IdentifyView , AttendenceView {
    private static final int VID = 6997;
    private static final int PID = 288;
    private TextView textView = null;
    private ImageView imageView = null;
    private boolean bstart = false;
    private boolean isRegister = false;
    private int uid = 1;
    private byte[][] regtemparray = new byte[3][2048];  //register template buffer array
    private int enrollidx = 0;
    private byte[] lastRegTemp = new byte[2048];

    private FingerprintSensor fingerprintSensor = null;

    int threshold = 55;
    String templateFromDB;

    private final String ACTION_USB_PERMISSION = "com.zkteco.silkiddemo.USB_PERMISSION";

    ActivityMainBinding mainBinding;

    InsertPresenter insertPresenter;
    IdentifyPresenter identifyPresenter;
    AttendencePresenter attendencePresenter;

    String dateTime;
    String strBase64;

    byte[] regTemp;

    int emp_ID;


    EditText edt_id,edt_name,edt_designation,edt_Note;

    String deviceId , ipAddress;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mainBinding = DataBindingUtil. setContentView(this, R.layout.activity_main);
      //  setSupportActionBar(mainBinding.toolbar);
//
//       mainBinding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);

        edt_id = findViewById(R.id.edt_id);
        edt_name = findViewById(R.id.edt_name);
        edt_designation = findViewById(R.id.edt_designation);
        edt_Note = findViewById(R.id.edt_note);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dateTime =  formatter.format(date);

        insertPresenter = new InsertPresenter(this);
        identifyPresenter = new IdentifyPresenter(this);
        attendencePresenter = new AttendencePresenter(this);

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        imageView.startAnimation(animFadeIn);

        Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        imageView.startAnimation(animFadeOut);

        InitDevice();
        startFingerprintSensor();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void OnBnBegin(View view) throws FingerprintException
    {
        try {
            if (bstart) return;
            if(!validateEmployeeID())
                return;
            if(!validateEmployeeName())
                return;
            if(!validateEmployeeDesignation())
                return;
            if(!validateEmployeeNote())
                return;
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
                           // textView.setText("FakeStatus:" + fingerprintSensor.getFakeStatus());
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
                    final byte[] tmpBuffer = fpTemplate;
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
                                    textView.setText("Please press the same finger 3 times for the enrollment");
                                    return;
                                }
                                System.arraycopy(tmpBuffer, 0, regtemparray[enrollidx], 0, 2048);
                                enrollidx++;
                                if (enrollidx == 3) {
                                     regTemp = new byte[2048];
                                    if (0 < (ret = ZKFingerService.merge(regtemparray[0], regtemparray[1], regtemparray[2], regTemp))) {
                                        ZKFingerService.save(regTemp, "test" + uid++);
                                        System.arraycopy(regTemp, 0, lastRegTemp, 0, ret);

                                        String eID = edt_id.getText().toString();
                                        String eName = edt_name.getText().toString();
                                        String eDesignation = edt_designation.getText().toString();
                                        String eNote = edt_Note.getText().toString();

                                        deviceId = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
//                                        try {
//                                            ipAddress = Inet4Address.getLocalHost().getHostAddress();
//                                        } catch (UnknownHostException e) {
//                                            e.printStackTrace();
//                                        }

                                        ipAddress = ipv4Address();

                                        //Base64 Template
                                       strBase64 = "data:image/png;base64,/"+
                                               Base64.encodeToString(regTemp, 0, ret, Base64.NO_WRAP);

                                        int empID = 0;
                                        int verifyID = 100;

                                        try
                                        {
                                             empID = Integer.parseInt(eID);
                                        }
                                        catch (NumberFormatException e)
                                        {
                                            // handle the exception
                                        }
                                        
                                     //  int verifyID = ZKFingerService.verifyId(tmpBuffer,"9465968548");

                                       if(checkConnection()){
                                           insertPresenter.postApi(empID,verifyID,
                                                   strBase64,Arrays.toString(regTemp),dateTime,deviceId,ipAddress);
                                       }

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
                                byte[] bufids = new byte[256];

                                if(checkConnection())
                                    identifyPresenter.identifyAPI(Arrays.toString(tmpBuffer));

                                int ret = ZKFingerService.identify(templateFromDB.getBytes(),tmpBuffer, threshold, 1);

                               String eID = edt_id.getText().toString();
                               String eName = edt_name.getText().toString();
                               String eDesignation = edt_designation.getText().toString();
                               String eNote = edt_Note.getText().toString();


                                if (ret > 0) {
                                    String strRes[] = new String(bufids).trim().split("\t");
//                                    String id = "ZKTeco_20_01";
//                                    int verifyID = ZKFingerService.verifyId(bufids,id);
 //                                   String verifyID = Arrays.toString(strRes);
                                   // String verifyID = convertStringArrayToString(strRes,",");
                                    int verifyID = ZKFingerService.verifyId(tmpBuffer,strRes[0]);
//                                    int verifyID;
//                                    try {
//                                       verifyID = Integer.parseInt(strRes[0]);
//                                    }
//                                    catch (NumberFormatException e) {
//                                        verifyID = 0;
//                                    }

                                   //  int id =   ZKFingerService.get(tmpBuffer,strRes[0]);
//                                    insertPresenter.postApi
//                                            (eID,verifyID,strBase64,Arrays.toString(tmpBuffer),dateTime);



                                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
//                                    intent.putExtra("eID",eID);
//                                    intent.putExtra("verifyID",verifyID);
//                                    intent.putExtra("strBase64",strBase64);
//                                    intent.putExtra("template",Arrays.toString(regTemp));
//                                    intent.putExtra("dateTime",dateTime);
                                    startActivity(intent);
                                   // textView.setText("identify succ, userid:" + strRes[0] + ", score:" + strRes[1]);
                                } else {
                                    MotionToast.Companion.
                                            darkColorToast(MainActivity.this,
                                                    "Error",
                                                    "Identify fail!",
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(MainActivity.this,
                                                    R.font.montserrat_regular));
                                    textView.setText("Identify fail");
                                }
                                //Base64 Template
                                //String strBase64 = Base64.encodeToString
                                // (tmpBuffer, 0, fingerprintSensor.getLastTempLen(), Base64.NO_WRAP);
                            }
                        }
                    });
                }


            };
            fingerprintSensor.setFingerprintCaptureListener(0, listener);
            fingerprintSensor.startCapture(0);
            bstart = true;
            textView.setText("Start capture success");
        }catch (FingerprintException e)
        {
            textView.setText("Begin capture fail.errorcode:"+ e.getErrorCode()
                    + "err message:" + e.getMessage() + "inner code:" + e.getInternalErrorCode());
        }
    }

    public void OnBnStop(View view) throws FingerprintException
    {
        try {
            if (bstart)
            {
                //stop capture
                fingerprintSensor.stopCapture(0);
                bstart = false;
                fingerprintSensor.close(0);
                textView.setText("Stop capture succ");
            }
            else
            {
                textView.setText("Already stop");
            }
        } catch (FingerprintException e) {
            textView.setText("Stop fail, errno=" + e.getErrorCode() + "\nmessage=" + e.getMessage());
        }
    }

    public void OnBnEnroll(View view) {
        if (bstart) {
            isRegister = true;
            enrollidx = 0;
            textView.setText("You need to press the 3 time fingerprint");
        }
        else
        {
            textView.setText("Please begin capture first");
        }
    }

    public void OnBnVerify(View view) {
        if (bstart) {
            isRegister = false;
            enrollidx = 0;
        }
        else {
            textView.setText("Please begin capture first");
        }
    }

    @Override
    public void onSuccess(InsertModel insertModel) {
        try {
            Toast.makeText(MainActivity.this,insertModel.getMessage(),Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(IdentifyModel identifyModel) {
        try {
            emp_ID = Integer.parseInt(identifyModel.getMessage().getEmp_id()) ;
            templateFromDB = identifyModel.getMessage().getTemp();
            if(checkConnection())
                attendencePresenter.attendenceAPI(emp_ID,deviceId,ipAddress,dateTime);
            Toast.makeText(MainActivity.this,identifyModel.getMessage().getEmp_id(),Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(AttendenceModel attendenceModel) {
        try {
            Toast.makeText(MainActivity.this,attendenceModel.getMessage(),Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this,"success",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String error, int code) {
        Toast.makeText(MainActivity.this,error,Toast.LENGTH_LONG).show();
    }

    private boolean validateEmployeeID() {
        String eID = edt_id.getText().toString().trim();
        if (TextUtils.isEmpty(eID)) {
            edt_id.setError("Employee ID is empty");
            requestFocus(edt_id);
            return false;
        }
        return true;
    }

    private boolean validateEmployeeName() {
        String eName = edt_name.getText().toString().trim();
        if (TextUtils.isEmpty(eName)) {
            edt_name.setError("Employee Name is empty");
            requestFocus(edt_name);
            return false;
        }
        return true;
    }

    private boolean validateEmployeeDesignation() {
        String eDesignation = edt_designation.getText().toString().trim();
        if (TextUtils.isEmpty(eDesignation)) {
            edt_designation.setError("Employee Designation is empty");
            requestFocus(edt_designation);
            return false;
        }

        return true;
    }

    private boolean validateEmployeeNote() {
        String eNote = edt_Note.getText().toString().trim();
        if (TextUtils.isEmpty(eNote)) {
            edt_Note.setError("Employee Note is empty");
            requestFocus(edt_Note);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private String ipv4Address(){
//        String ip = null;
//        try {
//            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//            while (interfaces.hasMoreElements()) {
//                NetworkInterface iface = interfaces.nextElement();
//                // filters out 127.0.0.1 and inactive interfaces
//                if (iface.isLoopback() || !iface.isUp())
//                    continue;
//
//                Enumeration<InetAddress> addresses = iface.getInetAddresses();
//                while(addresses.hasMoreElements()) {
//                    InetAddress addr = addresses.nextElement();
//
//                    // *EDIT*
//                    if (addr instanceof Inet4Address) continue;
//
//                    ip = String.valueOf(addr.getHostAddress().hashCode());
//                    System.out.println(iface.getDisplayName() + " " + ip);
//                }
//            }
//        } catch (SocketException e) {
//            throw new RuntimeException(e);
//        }

        Context context = this.getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return  ip;
    }


}
