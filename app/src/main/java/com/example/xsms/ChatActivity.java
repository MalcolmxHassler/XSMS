package com.example.xsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.ModelChat;


public class ChatActivity extends AppCompatActivity {

    private static final String MSG_TYPE_LEFT="0";
    private static final String MSG_TYPE_RIGHT="1";
    Context context;

    List<ModelChat> chatList;

    //views from layoutXML
    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView name, number;
    EditText sendMessage;
    ImageButton sendBtn;
    ImageButton returnBtn;

    ArrayList<String> messageList, dateList;
    ArrayList<Integer>  statusList;
    //components of Send&Receive Message
    TextView messageRec,timeRec;

    //Instance of DB
    DatabaseHelper myDB;

    //declaration adapter pour afficher les messages
    AdapterMessagePerso adapterMessagePerso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        myDB = new DatabaseHelper(this);

        //initialisation
        messageList = new ArrayList<String>();
        dateList = new ArrayList<String>();
        statusList = new ArrayList<Integer>();

        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        final String num = intent.getStringExtra("numero");

        recyclerView  = (RecyclerView) findViewById(R.id.chat_recyclerView);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        sendMessage = findViewById(R.id.SendMessage);
        sendBtn = findViewById(R.id.SendBtn);
        returnBtn = findViewById(R.id.returnbtn);

        //Setting the text where necessary
        name.setText(nom);
        number.setText(num);

        //Components for send&Receive Message
        messageRec = findViewById(R.id.messageRec);
        timeRec = findViewById(R.id.timeRec);

        //Layout (LinearLayout) for RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //recyclerproperties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Cursor data = myDB.getConversation(num);

        //Cursor data = myDB.getData();

        while (data.moveToNext()){
            String message = data.getString(2);
            String dateM = data.getString(5);
            int status = data.getInt(6);

            messageList.add(message);
            dateList.add(dateM);
            statusList.add(status);

        }

        adapterMessagePerso = new AdapterMessagePerso(getApplicationContext(), messageList, dateList, statusList);

        recyclerView.setAdapter(adapterMessagePerso);




        //send the sms
        sendBtn.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                //start for calculation
                long start = System.nanoTime();
                int random = Security.generateRandomData();
                String secretKey = Security.generateKey(random);

                String contact = num; // get the contact
                String messageContent = sendMessage.getText().toString(); // get the message
                if (contact.length() > 0 && secretKey.length() > 0 && messageContent.length() > 0 && secretKey.length() == 16){

                    //encrypt the message
                    byte[] encryptedString = Security.encryptSMS(secretKey,messageContent);

                    //convert the byte array to hex for transmission
                    String FinalEncMsg = Security.byte2hex(encryptedString);

                    //complete MSG to Send
                    String HexRandom = Integer.toHexString(random);
                    String Hexlength = Integer.toHexString(HexRandom.length());
                    String Flag = Security.ConvertFlag("XSMS");
                    String finalMsg =Flag+Hexlength+HexRandom+FinalEncMsg;

                    //end of the decryption
                    long encryptime = System.nanoTime()-start;
                    long millsecond = encryptime/1000000;

                    //Send the SMS
                    boolean send = SmsSender.sendSMS(contact,finalMsg);

                    //the timestamp to identifie a message
                    long timestamp= DatabaseHelper.getTimestamp();

                    //Verification apres l'envoi
                    if(send){
                        //inserting the message into the DB
                        boolean verify = myDB.CheckIfExist(contact,getBaseContext());
                        if(verify){
                            myDB.updateDataM(contact,messageContent, myDB.getDateTime(),timestamp, getBaseContext());
                            myDB.AddDataC(contact,messageContent,FinalEncMsg,secretKey,1,timestamp,getBaseContext());

                        }else{
                            myDB.AddDataM(contact,messageContent,1,timestamp,getBaseContext());
                            myDB.AddDataC(contact,messageContent,FinalEncMsg,secretKey,1,timestamp,getBaseContext());
                        }

                        //number.setText("");
                        sendMessage.setText("");
                        //Toast.makeText(getBaseContext(),"Message Sent", Toast.LENGTH_SHORT).show();
                       messageList.add(messageContent);
                       dateList.add(myDB.getDateTime());
                       statusList.add(1);
                       adapterMessagePerso.notifyDataSetChanged();

                        //MainActivity inst = MainActivity.instance();
                        //inst.updateList();
                        //go Back to the main Activity
                        //Intent intent = new Intent(NewChatActivity.this,MainActivity.class);
                        //startActivity(intent);

                        //clearing the memory
                        Hexlength=null;HexRandom=null; finalMsg=null;FinalEncMsg=null; contact=null;messageContent=null;
                    }else {
                        Toast.makeText(getBaseContext(), "Message Not Sent", Toast.LENGTH_SHORT).show();
                    }

                }else
                    Toast.makeText(getBaseContext(), "Enter a valid Number", Toast.LENGTH_SHORT).show();


            }
        });


        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }








}
