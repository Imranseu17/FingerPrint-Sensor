package com.zkteco.silkiddemo.service;

import android.content.Context;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.zkteco.android.biometric.module.fingerprintreader.ZKFingerService;
import com.zkteco.silkiddemo.R;
import com.zkteco.silkiddemo.activity.MainActivity;
import com.zkteco.silkiddemo.model.Message;
import com.zkteco.silkiddemo.view.DataView;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class VerificationService  {

     boolean verified;
    ArrayList<Message> messages;
    int threshold = 55;
    int matchScoreCompareValue = 23;
    int matchScore;
    byte[] currentFingerPrint;
    byte[] dbFingerPrint;
    int empID;
    String eName;

    // Debug
    Context context;


    public VerificationService(ArrayList<Message> messages, Context context) {
        this.verified = false;
        this.messages = messages;

        // Debug
        this.context = context;
    }

    public byte[] getCurrentFingerPrint() {
        return currentFingerPrint;
    }

    public byte[] getDbFingerPrint() {
        return dbFingerPrint;
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        this.empID = empID;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int verify(byte[] currentFingerprint, byte[] dbFingerprint){

        int matchScore = ZKFingerService.verify(currentFingerprint,dbFingerprint);
        int identifyScore = ZKFingerService.identify(currentFingerprint,dbFingerprint,threshold,1);

        if(matchScore > matchScoreCompareValue){
            this.matchScore = matchScore;
            return matchScore;
        }

        return 0;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public boolean isVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void messageReceived(byte[] pressedFingerprint){
        this.currentFingerPrint = pressedFingerprint;
     //  Toast.makeText(this.context,"In messegeReceived", Toast.LENGTH_LONG).show();
        Message message;
        int index = 0;
        Gson gson = new Gson();
   // Toast.makeText(this.context,"success: " + this.getEmpID(),Toast.LENGTH_LONG).show();
        while (messages.size() > 0){
            message = messages.get(index);
          //  Toast.makeText(this.context,"In Loop [message] = " + message.getEmp_id(), Toast.LENGTH_LONG).show();
            final String templateFromDB = message.getTemp();
            this.dbFingerPrint = gson.fromJson(templateFromDB, byte[].class);
            int resultScore = verify(this.currentFingerPrint, this.dbFingerPrint);
           // Toast.makeText(this.context,"In Loop [resultScore] = " + resultScore, Toast.LENGTH_LONG).show();
            if(resultScore > 0){
                try {
                    this.empID = Integer.parseInt(message.getEmp_id());
                    this.eName = message.getName();
                   // Toast.makeText(this.context,"In Loop [try->set->empID] = " + this.empID, Toast.LENGTH_LONG).show();
                    this.verified = true;
                    break;
                }catch (Exception e){
                   Toast.makeText(this.context,"In Loop [exception] = " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            } else {
                this.verified = false;
              //  Toast.makeText(this.context,"In Loop [resultScore on ZERO] = This finger is not verified", Toast.LENGTH_LONG).show();
            }
            index++;
           // Toast.makeText(this.context,"In Loop [index] = " + index, Toast.LENGTH_LONG).show();
        }

    }
}
