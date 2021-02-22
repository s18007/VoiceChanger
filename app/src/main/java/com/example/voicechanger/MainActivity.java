package com.example.voicechanger;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int w = 0;
    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "/DCIM/AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private String saveFileName = null;

    private AudioRecord recorder = null;
    private int bufferSize = 512;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private SaveData save = SaveData.getInstance();

    private Chronometer chronometer;
    private ImageButton micbtn;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        chronometer = findViewById(R.id.chronometer);
        micbtn = findViewById(R.id.micbtn);

        micbtn.setOnClickListener( v -> {

            if(isRecording){
                isRecording = false;
                chronometer.stop();
                stopRecording();
                Intent intent = new Intent(MainActivity.this, Effect.class);
                startActivity(intent);
            } else {
                isRecording = true;
                startRecording();
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });
    }
    private AudioRecord getAudioRecord(){
        for(int rate:new int[]{8000, 11025, 16000, 22050, 44100}){
            for(short audioFormat:new short[]{AudioFormat.ENCODING_PCM_8BIT,AudioFormat.ENCODING_PCM_16BIT}){
                for(short channelConfig : new short[]{AudioFormat.CHANNEL_IN_MONO,AudioFormat.CHANNEL_IN_STEREO}){
                    try{
                        int buffSize = AudioRecord.getMinBufferSize(rate,channelConfig,audioFormat);
                        if(buffSize != AudioRecord.ERROR_BAD_VALUE){
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,rate,channelConfig,audioFormat,buffSize);

                            if(recorder.getState() == AudioRecord.STATE_INITIALIZED){

                                return recorder;
                            }
                        }
                    }catch (Exception e){

                    }
                }
            }
        }
        return null;
    }
    private String getFilename() {
        /*String filepath = Environment.getExternalStorageDirectory().getPath();*/
        File filepath = new File(Environment.getExternalStorageDirectory().getPath());
        File file = new File(filepath, System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV/*AUDIO_RECORDER_FOLDER*/);


        if (!file.exists()) {
            filepath.mkdirs();
        }


        return (filepath + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();
        /*tempFile.delete();*/

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);

        int i = recorder.getState();
        System.out.println("bufferedsize: " + bufferSize);
        if (i == 1)
            recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();

    }


    private void writeAudioDataToFile(){
        short data[] = new short[bufferSize];

        String filename = getTempFilename();
        FileOutputStream os = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;

        try {
            os = new FileOutputStream(filename);
            bos = new BufferedOutputStream(os);
            dos = new DataOutputStream(bos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;
        ArrayList<Short> ar = new ArrayList<Short>();
        if (null != dos) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        for(int t = 0; t < data.length; t++) {
                            dos.writeShort(data[t]);
                        }
                        w++;
                        save.setData(data);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            //Float[] source = ar.toArray(new Float[ar.size()]);
            //save.setData(source);
            try {
                os.close();
                bos.close();
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            save.setLength(bufferSize);
            save.setQa(RECORDER_BPP);
            save.setRate(RECORDER_SAMPLERATE);
        }
    }

    private void stopRecording() {
        if (null != recorder) {

            isRecording = false;

            int i = recorder.getState();
            if (i == 1)
                recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }
        saveFileName = getFilename();
        //copyWaveFile(getTempFilename(), saveFileName);
        deleteTempFile();
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }
/*
    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        short[] data = new short[bufferSize * (w + 1)];
        double[] effect = new double[bufferSize * (w + 1)];
        short s;
        boolean EOF = false;


        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            //お試し

            bis = new BufferedInputStream(in);
            bos = new BufferedOutputStream(out);
            dis = new DataInputStream(bis);
            dos = new DataOutputStream(bos);

            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;


            WriteWaveFileHeader(dos, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);



            System.out.println("buffersize: " + bufferSize);
            System.out.println("effect.length: " + effect.length);

            //byte[] effect = new byte[data.length * (w + 1)];
            int c = 0;

            while(!EOF) {
                dos.writeShort(s = dis.readShort());
                effect[c] = (double)s / 32768.0;

                c++;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            EOF = true;
            try {
                in.close();
                bis.close();
                dis.close();
                out.close();
                bos.close();
                dos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            save.setData(effect);
        } catch (IOException ef) {
            ef.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            DataOutputStream dos, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // ‘fmt ‘ chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of ‘fmt ‘ chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        dos.write(header, 0, 44);
        save.setHeader(header);
    }

 */

}