package com.example.voicechanger;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.effect.EffectContext;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.Viewholder>{
    private List<ViewData> DataList;
    WavSave asWav = new WavSave();
    ViewAdapter(List<ViewData> DataList){
        this.DataList = DataList;
    }
    private EffecterList effecter = EffecterList.getInstance();
    @Override
    public ViewAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list,parent,false);
        return new Viewholder(view);
    }
    @Override
    public void onBindViewHolder(ViewAdapter.Viewholder holder, final int position) {
        final ViewData data = DataList.get(position);

        holder.Icon_name.setImageResource(data.getImage_name());
        holder.Label.setText(data.getText());

        final int p = position;

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(p) {
                    case 0:
                        System.out.println("Echo");
                        effecter.ThreadStart("Echo");
                        break;
                    case 1:
                        System.out.println("Vibrato");
                        effecter.ThreadStart("Vibrato");
                        break;
                    case 2:
                        System.out.println("Tremolo");
                        effecter.ThreadStart("Tremolo");
                        break;
                    case 3:
                        effecter.ThreadStart("Electric");
                        break;
                    case 4:
                        effecter.ThreadStart("Radio");
                        break;
                    case 5:
                        effecter.ThreadStart("sin2π");
                        break;
                    case 6:
                        effecter.ThreadStart("sin5π");
                        break;

                }
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(p) {
                    case 0:
                        try {
                            asWav.Robot();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                Toast.makeText(v.getContext(), p + ": " + "SAVEボタン", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return DataList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView Icon_name;
        private TextView Label;
        private ImageButton save;
        private ImageButton play;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            Icon_name = itemView.findViewById(R.id.Icons);
            Label = itemView.findViewById(R.id.IconLabels);
            save = itemView.findViewById(R.id.Savebtn);
            play = itemView.findViewById(R.id.Playbtn);
        }
    }


}
