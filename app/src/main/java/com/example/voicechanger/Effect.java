package com.example.voicechanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Effect extends AppCompatActivity {
    private ArrayList<ViewData> DataList;
    private RecyclerView rec;

    private SaveData save = SaveData.getInstance();
    private EffecterList effecter = EffecterList.getInstance();
    ImageButton play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);

        effecter.initialize();

        rec = findViewById(R.id.Other);
        DataList = new ArrayList<>();
        DataList.add(new ViewData(R.drawable.echo, "エコー"));
        DataList.add(new ViewData(R.drawable.vibrato, "ビブラート"));
        DataList.add(new ViewData(R.drawable.tremolo, "トレモロ"));
        DataList.add(new ViewData(R.drawable.elec, "エレキギター"));
        DataList.add(new ViewData(R.drawable.radio, "拡声器"));
        DataList.add(new ViewData(R.drawable.two, "2Hzの波合成"));
        DataList.add(new ViewData(R.drawable.five, "5Hzの波合成"));


        ViewAdapter viewadapter = new ViewAdapter(DataList);
        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setAdapter(viewadapter);

        play = (ImageButton)findViewById(R.id.PlayButton);
        save.ChangeType();
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                effecter.ThreadStart("Normal");

            }
        });
    }

}