package com.example.xsms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xsms.beans.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //For the permissions
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;


    //gestion de la BD
    DatabaseHelper databaseHelper;

    //pour le test d'insertion
    ListView mListview;
    static RecyclerView recyclerView;
    CustomListView.RecyclerViewClickListener listener;

    //pour la BD
    ArrayList<Message> mesMessages;
   static ArrayList<String> contactName, dateMessage, contentMessage, contactNumber;
    static CustomListView customListView;
    static ArrayList<String> listContact;

    private Parcelable recyclerViewState;


    private static MainActivity inst;
    private String TAG;
    ArrayList<String> listData;


    public static MainActivity instance() {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //calling the splashscreen
        //setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mesMessages = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        //mListview = (ListView) findViewById(R.id.listview);
        recyclerView = findViewById(R.id.mainPage);
        listContact = new ArrayList<>();

        //Check if the permission is not granted
        //TODO : If permission is not granted
        if(ContextCompat.checkSelfPermission(getBaseContext(),"android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED){
            //TODO : If Permission Granted then display SMS


        }else{
            //TODO : Then set the permission
            final int REQUEST_CODE_ASK_PERMISSIONS=123;
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{"android.permission.RECEIVE_SMS","android.permission.READ_SMS","android.permission.READ_CONTACTS"},REQUEST_CODE_ASK_PERMISSIONS);
        }

        //method
        populateListview();
        customListView = new CustomListView(MainActivity.this, mesMessages);
        recyclerView.setAdapter(customListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);




        /** Calling the Floating Button to start a New activity **/

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //going to the new view/activity
                //Toast.makeText(MainActivity.this,"Tab Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NewChatActivity.class);
                startActivity(intent);
            }
        });

    }


    private void setOnClickListner() {
        listener = new CustomListView.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("contact",contactName.get(position));
                intent.putExtra("number",dateMessage.get(position));
            }
        };
    }


    private void populateListview() {
        Log.d(TAG, "Populate Displaying data in the ListView");
        //Get the data and Append to the list
        Cursor data = databaseHelper.getData();
       // listData = new ArrayList<>();
        if(data.getCount() == 0){
            Toast.makeText(this,"No Message Sent or Received",Toast.LENGTH_SHORT).show();
        }else {
            while (data.moveToNext()) {
                Message mes;
                //pour recuperer les messages dans la BD
               // String Contact = data.getString(1);
                String number = data.getString(1);
                String cipher = data.getString(2);
               if(cipher.length()>=0 && cipher.length() <=30){
                   cipher = cipher;
               }else{
                   cipher = cipher.substring(0,30)+"...";
               }
                String date = data.getString(3);

                //for the numbers
                String num = SmsSender.correctNumber(number);

                Map<String, String> ContactName = SmsSender.getContactName(getBaseContext(), listContact);

                //checking if its null
                String FinalName = ContactName.get(num);
                if(FinalName == null){
                    FinalName = num;
                }
                mes = new Message(FinalName, num, cipher, date);
                mesMessages.add(mes);
            }

        }
    }


    public void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void updateList( String FinalName, String num, String cipher, String date) {
                mesMessages.add(new Message(FinalName, num, cipher, date));
                customListView.notifyDataSetChanged();
            }

}

