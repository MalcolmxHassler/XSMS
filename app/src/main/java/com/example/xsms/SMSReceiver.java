package com.example.xsms;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
//import Model.MessageL;

public class SMSReceiver  extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    //gestion de la BD
    DatabaseHelper myDb;

    @Override
    public void onReceive(Context context, Intent intent) {
        myDb = new DatabaseHelper(context);
        //path to save the files
         final  String FILE_NAME="decrypt.txt";


            //New Test pour la reception
            Bundle bundle = intent.getExtras();
        // Specify the bundle to get object based on SMS protocol "pdus"
        Object[] object = (Object[]) bundle.get("pdus");
        SmsMessage sms[] = new SmsMessage[object.length];
        String msgContent = "";
        String originNum = "";

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < object.length; i++) {
            sms[i] = SmsMessage.createFromPdu((byte[]) object[i]);
            // get the received SMS content
            msgContent = sms[i].getDisplayMessageBody();
            // get the sender phone number
            originNum = sms[i].getDisplayOriginatingAddress();
            // aggregate the messages together when long message are fragmented
            sb.append(msgContent);
            // abort broadcast to cellphone inbox
            abortBroadcast();
        }
        // to  fill into the DB
        String msgEnc = new String(sb);
        String msgClair = "";
        String secret = "1111111111111111";
        // to calculate the time it take for decryption
        long start = System.nanoTime();
        //Applying x-Talk protocol to get the message
        if(msgEnc.startsWith(Security.ConvertFlag("XSMS"))){
            int Hexlength = Integer.parseInt(msgEnc.substring(40,41)); //convert to decimal
            int HexRandom = Integer.parseInt(msgEnc.substring(41,41+Hexlength),16); //convert to decimal
            msgEnc= msgEnc.substring(Hexlength+41);
            secret = Security.generateKey(HexRandom);
        }


        long timestamp= DatabaseHelper.getTimestamp();

        //decryption fo the SMS Received
        if (secret.length() > 0 && secret.length()==16){
            try{
                //convert the encrypted String message

                byte[] msg =Security.hex2byte(msgEnc.getBytes());
                byte[] result = Security.decryptSMS(secret.toString(), msg);
             msgClair = new String(result);
            }catch (Exception e){
                //In case the message corrupted or invalid Key
                msgClair = msgEnc;
            }
        }else {
            Toast.makeText(context, "Invalid Message", Toast.LENGTH_SHORT).show();
        }
        String finalNum = SmsSender.correctNumber(originNum);

        //end of the decryption
        long decryptime = System.nanoTime()-start;
        long millsecond = decryptime/1000000;

        //verification before inserting
        boolean verify = myDb.CheckIfExist(finalNum, context);
        if (verify) {
            //if the number exist into the DB
            //insert-Update into the table Message
            String time = myDb.getDateTime();
           myDb.updateDataM(finalNum, msgClair, time, timestamp,context);
            //insert-Update into the table Conversation
            myDb.AddDataC(finalNum, msgClair, msgEnc, secret, 0,timestamp, context);
        } else { //if the number does not exist into the DB
            //insert-Update into the table Message
            myDb.AddDataM(finalNum, msgClair, 0,timestamp,context);
            //insert-Update into the table Conversation
            myDb.AddDataC(finalNum, msgClair, msgEnc, secret, 0, timestamp,context);
        }
    }



}