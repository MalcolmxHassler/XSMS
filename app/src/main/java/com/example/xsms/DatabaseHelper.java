package com.example.xsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class DatabaseHelper  extends SQLiteOpenHelper {
    //initialize variable
    private static final String DATABASE_NAME = "xsms.db"; //name of the database
    private static final String TABLE1 = "conversation";
    private static final String TABLE2 = "message";
   // private static final String TABLE3 = "contact";

 public   DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the tables
        String table1 = "CREATE TABLE "+TABLE1+"(id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT, message TEXT, cipherText TEXT, cle TEXT, date_msg TEXT, status INTEGER, timestamp LONG)";
        String table2 = "CREATE TABLE "+TABLE2+"(id INTEGER PRIMARY KEY AUTOINCREMENT,number TEXT,message TEXT,date_msg TEXT, timestamp LONG)";
       // String table3 = "CREATE TABLE "+TABLE3+"(id INTEGER PRIMARY KEY AUTOINCREMENT, contact TEXT, number TEXT)";

        db.execSQL(table1);
        db.execSQL(table2);
        //db.execSQL(table3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DROP Existing table
        db.execSQL("DROP TABLE IF EXISTS "+TABLE1);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE2);
        //db.execSQL("DROP TABLE IF EXISTS "+TABLE3);
        onCreate(db);
    }

    //Create insert Method insertion into table conversation
    public boolean insertConversation(String number, String message,String cipherText,String secretKey, int status, long timestamp,Context context){

        //Get writeAble Database
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        //Create contentValues
        ContentValues values1 = new ContentValues();
        values1.put("number",number);
        values1.put("message",message);
        values1.put("cipherText",cipherText);
        values1.put("cle",secretKey);
        values1.put("date_msg", getDateTime());
        values1.put("status",status);
        values1.put("timestamp",timestamp);
        //insertion
        long result = sqLiteDatabase.insert(TABLE1, null, values1);

        if(result == -1)
            return false;
        else
            return true;
    }

    //insert into Message Table
    public boolean insertMessage(String number, String message,int status, long timestamp, Context context){
        //Get writeAble Database
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        //Create contentValues
        ContentValues values1 = new ContentValues();
        values1.put("timestamp",timestamp);
        values1.put("number",number);
        values1.put("message",message);
        values1.put("date_msg",getDateTime());
        //insertion
       long result = sqLiteDatabase.insert(TABLE2, null, values1);

       if(result == -1)
           return false;
       else
           return true;

    }

    //update the table message if the number already exist
    public boolean updateMessageTable(String number, String msg, String dateMsg, long timestamp, Context context){
     DatabaseHelper databaseHelper = new DatabaseHelper(context);
     SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
     ContentValues values = new ContentValues(); //contact TEXT, number TEXT,cipherText TEXT,date_msg TEXT
     values.put("message",msg);
     values.put("date_msg",dateMsg);
     values.put("timestamp",timestamp);
     long result = sqLiteDatabase.update(TABLE2,values,"number=?",new String[] {number});
     return true;
    }

    //Check if it is already into the dataBase
    public boolean CheckIfExist(String numb, Context context){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = " SELECT EXISTS(SELECT * FROM "+ TABLE2 + " WHERE number = '" + numb+"'LIMIT 1)";
        Cursor cursor = sqLiteDatabase.rawQuery(Query,null);
        cursor.moveToFirst();
       if(cursor.getInt(0) == 1){
            cursor.close();
            return true;
        }
       cursor.close();
       return false;
    }



    public static String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Method that return all data from the database
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE2 +" ORDER BY date_msg DESC ";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    // Method to get the timestamp
    public static long getTimestamp(){
     Date date= new Date();
     return date.getTime();
    }

    //Method that return all conversation from the database
    public  Cursor getConversation(String num){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE1 +" WHERE number = '"+ num +"'  ORDER BY date_msg  ";
        Cursor data = db.rawQuery(query,null);
        return data;
    }




    // Fonctions for inserting and updating data


    //to ADD data
    //Add data into the table Message
    public  void AddDataM(String number, String message, int status, long timestamp, Context context){
        boolean insertDate = this.insertMessage(number,message,status, timestamp,context );
        if(insertDate){
            Toast.makeText(context, "New Message Received", Toast.LENGTH_SHORT).show();
            //MainActivity inst = MainActivity.instance();
            //inst.updateList(contactName, number, message, this.getDateTime());
        }else{
            Toast.makeText(context, "Erreur Lors de la reception", Toast.LENGTH_SHORT).show();
        }
    }

    //Add data into the table conversation
    public void AddDataC(String number,String message,String cipher, String secretkey, int status, long timestamp, Context context){
        boolean insertDate = this.insertConversation(number,message,cipher,secretkey,status,timestamp,context );
        if(insertDate){
            Toast.makeText(context, "New Message Received", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Erreur Lors de la reception", Toast.LENGTH_SHORT).show();
        }
    }

    //call the update function to verify if update
    public void updateDataM(String number, String msg, String dateMsg, long timestamp, Context context){
        boolean verify = this.updateMessageTable(number,msg,dateMsg,timestamp,context);
        if(verify){
            Toast.makeText(context, "New Message", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Erreur Lors de la reception", Toast.LENGTH_SHORT).show();
        }
    }






}
