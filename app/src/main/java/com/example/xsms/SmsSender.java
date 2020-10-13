package com.example.xsms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmsSender {

    public static boolean sendSMS(String Number, String encryptedMsg) {

        try {
            // get a SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            // Message may exceed 160 characters
            // need to divide the message into multiples
            ArrayList<String> parts = smsManager.divideMessage(encryptedMsg);

            smsManager.sendMultipartTextMessage(Number, null, parts, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // Function to get the Name of a contact Map<number,ContactName>

    static Map<String, String> getContactName(Context context, ArrayList<String> listContact){
        List<String> testcontenu =new ArrayList<>();
        Map<String, String> contact = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            contact = new ArrayMap<>();
        }
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try{
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            cursor = contentResolver.query(uri, projection, null, null, null);
            //cursor_Android_contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        }catch (Exception e){
            Log.e("Error on contact",e.getMessage());
        }
        if(cursor.moveToFirst()){
            do{
                String num = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_ALTERNATIVE));

                String finalNum = correctNumber(num);
                finalNum = finalNum.replaceAll("\\s+","");
                listContact.add(name+"_"+finalNum);
                if(!testcontenu.contains(num)){
                    contact.put(finalNum, name);
                    testcontenu.add(finalNum);
                }

            }while(cursor.moveToNext());
        }
        return contact;
    }

    static String correctNumber(String number) {
        String[] ind = {"+237","+33","+1","00237","001"};
            for (int i = 0; i <= ind.length; i++){
                try {
                    if (number.contains(ind[i])){
                        return number.replace(ind[i], "");
                    }
                }
                catch(Exception e){}
            }
        return number;
    }


}
