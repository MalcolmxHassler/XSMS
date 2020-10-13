package com.example.xsms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;

public class NewChatActivity extends AppCompatActivity {

    ImageButton sendBtn;
    EditText number, sendMsg;
    DatabaseHelper myDB;
    Spinner spin;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        sendMsg = findViewById(R.id.SendMessage);
         number = findViewById(R.id.newNumber);
         sendBtn = findViewById(R.id.SendBtn);
         spin = findViewById(R.id.idspinner);

         ArrayList<String> ListNumero = MainActivity.listContact;
        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ListNumero);
        spin.setAdapter(adapter);


        //send the sms
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    boolean send = SmsSender.sendSMS(number.getText().toString(),sendMsg.getText().toString());
                    if(send) {
                        Toast.makeText(getBaseContext(), "Sent message", Toast.LENGTH_SHORT).show();
                        number.setText("");
                        sendMsg.setText("");
                    }
                    else
                        Toast.makeText(getBaseContext(), "Not Sent", Toast.LENGTH_SHORT).show();


            }
        });


    }






}
