package com.example.xsms;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xsms.beans.Message;

import java.util.ArrayList;

public class CustomListView extends RecyclerView.Adapter<CustomListView.MyViewHolder> {

    private Context context;
    ArrayList<Message> mesMessages;
    private RecyclerViewClickListener listener;
    private MyViewHolder holder;

    CustomListView(Context context, ArrayList<Message> mesMessages){
        this.context=context;
        this.mesMessages = mesMessages;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(context);
       View view = inflater.inflate(R.layout.rowmainpage,parent,false);

       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.contactName1.setText(mesMessages.get(position).getNom());
        holder.dateMessage1.setText(mesMessages.get(position).getDateM());
        holder.contentMessage1.setText(mesMessages.get(position).getMessage());
        holder.number1 = mesMessages.get(position).getNumero();
        this.holder = holder;
    }

    @Override
    public int getItemCount() {
        return mesMessages.size();
    }

    public interface  RecyclerViewClickListener{
        void onClick(View v, int position);
    }

   /* public void insertItem(String contact, String dateM, String content, String num){
        contactNumber.add(num);
        contentMessage.add(content);
        contactName.add(contact);
        dateMessage.add(dateM);
        notifyDataSetChanged();

    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView contactName1, dateMessage1, contentMessage1;
        String number1;

        public MyViewHolder(final View itemView) {
            super(itemView);
            number1 = "";
            contactName1 = itemView.findViewById(R.id.getContactName);
            dateMessage1 = itemView.findViewById(R.id.getdate);
            contentMessage1 = itemView.findViewById(R.id.getMessageContent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("numero", number1);
                    intent.putExtra("nom", contactName1.getText());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            //listener.onClick(v, getAdapterPosition());
        }
    }
}
