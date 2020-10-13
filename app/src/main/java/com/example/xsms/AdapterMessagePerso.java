package com.example.xsms;


import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterMessagePerso extends RecyclerView.Adapter<AdapterMessagePerso.ViewHolder> {

    List<String> mesMessage;
    List<String> mesDate;
    List<Integer> status;
    Context context;
    //les modifications pour afficher les vues
    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    public AdapterMessagePerso(Context context, List<String> mesMessage, List<String> mesDate, List<Integer> status) {
        this.mesMessage = mesMessage;
        this.mesDate = mesDate;
        this.status = status;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new ViewHolder(view);
        }
        // return null;
        //fin de la modification parraport au tutoriel


    }

    //Jai aussi modifier ici
    @Override
    public void onBindViewHolder(@NonNull AdapterMessagePerso.ViewHolder holder, int position) {
        holder.message.setText(mesMessage.get(position));
        holder.date.setText(mesDate.get(position));


    }



    @Override
    public int getItemCount() {
        //  return 0; //Jai aussi modifier ici
        return mesMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView message;
        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageRec);
            date = itemView.findViewById(R.id.timeRec);
        }

        @Override
        public void onClick(View v) {

        }
    }

    //jai aussi modifier ici
    @Override
    public int getItemViewType(int position) {
        // if (mesMessage.get(position).getDirection() == 1) return 1;
        //else return 0;
        if(status.get(position).equals(1)) return MSG_TYPE_RIGHT;
        else {
            return MSG_TYPE_LEFT;
        }
    }



    public void updateData(List<String> songList) {
        this.mesMessage.clear();
        this.mesMessage.addAll(songList);
        notifyDataSetChanged();
    }

}

