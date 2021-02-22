package com.example.voicechanger;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class EffecterList implements Runnable{
    private static EffecterList instance = new EffecterList();

    double depth = 1.0;
    double rate = 150.0; /* 150Hz */

    private Thread thread = null;
    final static String TAG = "MyAudioTrackService";
    public static final int WRITE_NON_BROCKING = 1;
    AudioTrack audioTrack; //再生用のクラス
    private byte[] playByteData = new byte[4096];
    private int playBufSize; //再生用バッファサイズ
    private FileInputStream in = null;
    //private String STATS = null;
    private String MODE = null;
    private Window window = new Window();
    private String getFileName = null;
    private AudioTrack AT = null;
    //byte[] music;
    private SaveData save = SaveData.getInstance();

    private String Mode = "";

    public static EffecterList getInstance(){
        return instance;
    }
    public void setMode(String Mode) {
        this.MODE = Mode;
    }

    private void Normal() throws Exception {


            playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    playBufSize, AudioTrack.MODE_STREAM);
            int i;
            AT.setPlaybackRate(88200);
            AT.play();

            double[] data = save.getData();
            short[] AudioData = new short[data.length];
            for(int y = 0; y < data.length; y++){
                AudioData[y] = Clipp(data[y]);
            }

            AT.write(AudioData, 0, AudioData.length);

            AT.stop();
            AT.release();

    }

    private void Echo() {

            playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    playBufSize, AudioTrack.MODE_STREAM);

            int k = 0;
            double a = 0.5; //減衰率
            double d = save.getRate() * 0.375; //遅延時間
            int repeat = 2; //繰り返し回数

            AT.setPlaybackRate(88200);

            short[] AudioData = new short[save.getData().length];

            double[] effect = save.getData();
            double[] y = new double[effect.length];

            for (int t = 0; t < effect.length; t++) {
                y[t] = effect[t];
                for (int m = 1; m <= repeat; m++) {
                    k = (int) ((double) t - (double) m * d);
                    if (k >= 0) {
                        y[t] += Math.pow(a, (double) m) * effect[k];
                    }
                }
                AudioData[t] = Clipp(y[t]);
            }

            AT.play();
            AT.write(AudioData, 0, AudioData.length);
            AT.stop();
            AT.release();


    }


    private void sin2π () {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];
        double[] data = save.getData();
        double[] effect = new double[data.length];
        for (int t = 0; t < data.length; t++) {
            double a = 1 * Math.sin(2.0 * Math.PI * rate * t / save.getRate());
            effect[t] = a * data[t];
            AudioData[t] = Clipp(effect[t]);
        }
        AT.write(AudioData, 0, AudioData.length);
        AT.stop();
        AT.release();
    }

    public void sin5π() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];
        double[] data = save.getData();
        double[] effect = new double[data.length];
        for (int t = 0; t < data.length; t++) {
            double a = 1 * Math.sin(5.0 * Math.PI * rate * t / save.getRate());
            effect[t] = a * data[t];
            AudioData[t] = Clipp(effect[t]);
        }
        AT.write(AudioData, 0, AudioData.length);
        AT.stop();
        AT.release();
    }

    public void Radio() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];
        IIR_filter filter = new IIR_filter();

        double[] data = save.getData();
        double[] effect = new double[data.length];
        short[] y = new short[data.length];
        double fc, Q, g;
        double[] a = new double[3];
        double[] b = new double[3];
        double[][] A = new double[3][3];
        double[][] B = new double[3][3];

        int i = 2; //遅延器
        int j = 2; //遅延器

        fc = 500.0/save.getRate(); //遮断周波数
        Q = 1.0/Math.sqrt(2.0); //クオリティファクタ
        g = -1.0;
        filter.IIR_low_shelving(fc, Q, g, a.length, b.length);
        a = filter.getA();
        b = filter.getB();
        for(int m = 0; m <= i; m++) {
            A[0][m] = a[m];
        }
        for(int m = 0; m <= j; m++) {
            B[0][m] = b[m];
        }

        fc = 1000.0/save.getRate();
        Q = 1.0/Math.sqrt(2.0); //中心周波数
        g = 1.0;
        filter.IIR_peaking(fc, Q, g, a.length, b.length);
        a = filter.getA();
        b = filter.getB();
        for(int m = 0; m <= i; m++) {
            A[1][m] = a[m];
        }
        for(int m = 0; m <= j; m++) {
            B[1][m] = b[m];
        }

        fc = 2000.0/save.getRate();
        Q = 1.0 / Math.sqrt(2.0);
        g = -1.0;
        filter.IIR_high_shelving(fc, Q, g, a.length, b.length);
        a = filter.getA();
        b = filter.getB();
        for(int m = 0; m <= i; m++) {
            A[2][m] = a[m];
        }
        for(int m = 0; m <= j; m++) {
            B[2][m] = b[m];
        }

        //イコライザ（３帯域）
        for(int u = 0; u < 3; u++) {
            for(int n = 0; n < data.length; n++) {
                for(int m = 0; m <= j; m++) {
                    if(n - m >= 0) {
                        effect[n] += B[u][m] * data[n-m];
                    }
                }
                for(int m = 1; m <= i; m++) {
                    if(n - m >= 0) {
                        effect[n] += -A[u][m] * effect[n-m];
                    }
                }
            }
            for(int n = 0; n < data.length; n++) {
                AudioData[n] = Clipp(effect[n]);
            }
        }
        AT.write(AudioData, 0, AudioData.length);
        AT.stop();
        AT.release();
    }
    //ビブラート処理
    public void Vibrato() {

        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];

        double[] data = save.getData();
        double[] effect = new double[data.length];
        short[] y = new short[data.length];
        double d = save.getRate() * 0.002;
        double depth = save.getRate() * 0.002;
        double rate = 5.0;

        for(int n = 0; n < data.length; n++) {
            double tau = d + depth * Math.sin(2.0 * Math.PI * rate * n / save.getRate());
            double t = (double)n - tau;
            int m = (int)t;
            double delta = t - (double)m;
            if(m >= 0 && m + 1 < data.length) {
                effect[n] = delta * data[m + 1] + (1.0 - delta) * data[m];
            }
            AudioData[n] = Clipp(effect[n]);
        }
        AT.write(AudioData, 0, AudioData.length);
        AT.stop();
        AT.release();
    }

    //コーラス
    public void Chorus() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];

        double[] data = save.getData();
        double[] effect = new double[data.length];

        double d = save.getRate() * 0.025;
        double depth = save.getRate() * 0.01;
        double rate = 0.1;

        int k = 0;
        double a = 0.5; //減衰率
        double delay = save.getRate() * 0.375; //遅延時間
        int repeat = 1; //繰り返し回数
        for(int n = 0; n < data.length; n++) {
            effect[n] = data[n];
            double tau = d + depth * Math.sin(2.0 * Math.PI * rate * n / save.getRate());
            double t = (double)n - tau;
            int m = (int)t;
            double delta = t - (double)m;
            if(m >= 0 && m + 1 < data.length) {
                effect[n] += delta * data[m + 1] + (1.0 - delta) * data[m];
            }
            for(int u = 1; u<=repeat; u++){
                k = (int)((double)n-(double)u*delay);
                if(k >= 0){
                    effect[n] += Math.pow(a, (double)u) * data[k];
                }
            }
            AudioData[n] = Clipp(effect[n]);
        }
        AT.write(AudioData, 0, AudioData.length);
        AT.stop();
        AT.release();
    }

    //トレモロ
    public void Tremolo() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];

        double[] data = save.getData();

        for(int n = 0; n < data.length; n++) {
            double a = 1.0 + depth * Math.sin(2.0 * Math.PI * rate * n / save.getRate());
            AudioData[n] = Clipp(a * data[n]);
        }
        AT.write(AudioData, 0, AudioData.length);
        AT.stop();
        AT.release();
    }

    //フランジャ
    public void Helium() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);

        AT.setPlaybackRate(88200);
        AT.play();

        short[] AudioData = new short[save.getData().length];
        double[] data = save.getData();
        double[] img = new double[data.length];
        FFT fft = new FFT();
        Random random = new Random();
        int frame_size = 512;
        double[] win_data = new double[frame_size];
        window.Hanning_window(win_data, frame_size);
        double[][] test = fft.STFT(data, data.length, win_data, frame_size);


    }

    public void Cepstrum(double[] formant, double[] fine){
        int frame_size = 512;
        double[] data = save.getData();
        double[] X_real = new double[data.length];
        double[] X_imag = new double[data.length];
        double[] hun = new double[data.length];
        double[] hunning = new double[frame_size];
        double[] x_real = new double[frame_size];
        double[] x_imag = new double[frame_size];
        double[] log_power = new double[frame_size];

        double[] log_pow = new double[data.length];

        double[] cepstrum = new double[data.length];
        FFT fft = new FFT();
        double[] new_data = new double[data.length];
        window.Hanning_window(hunning,frame_size);
        window.Hanning_window(hun, data.length);
            /*
            for(int offset = 0; offset < save.getData().length - frame_size; offset+=frame_size / 2){
                for(int n = offset; n < frame_size; n++) {
                    x_real[n] = data[n] * hunning[n]; //窓を掛ける
                }
                fft.fft(x_real, x_imag, frame_size);
                for(int k = offset; k < frame_size; k++) {
                    log_power[k] = Math.pow(Math.log10(Math.pow(Math.abs(x_real[k]), 2)), 10);
                }
                fft.fft(log_power, x_imag, frame_size);
                for(int l = 0; l < frame_size; l++) {
                    cepstrum[l] = log_power[l];
                }
            }
            */
        for(int n = 0; n < data.length; n++) {
            X_real[n] = data[n] * hun[n];
        }
        fft.DFT(X_real, X_imag, data.length);
        for(int k = 0; k < data.length; k++) {
            log_pow[k] = Math.pow(Math.log10(Math.pow(Math.abs(X_real[k]), 2)), 10);
        }
        fft.DFT(X_real, X_imag, data.length);
    }

    public void Electric() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);
        int i;
        AT.setPlaybackRate(88200);
        AT.play();


        //save.ChangeType();
        double[] data = save.getData();
        short[] AudioData = new short[data.length];
        double gain = 150.0; //増幅率
        double level = 0.5; //レベル
        double[] box = new double[data.length];
            for(int n = 0; n < box.length; n++) {
                box[n] = data[n] * gain;
                if(box[n] > 1.0) {
                    box[n] = 1.0;
                } else if(box[n] < -1.0) {
                    box[n] = -1.0;
                }
                box[n] *= level;
                AudioData[n] = Clipp(box[n]);
            }

        AT.write(AudioData, 0, AudioData.length);

        AT.stop();
        AT.release();
    }

    public void Slow() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);
        int i;
        AT.setPlaybackRate(44100);
        AT.play();


        save.ChangeType();
        double[] data = save.getData();

        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }

        AT.write(AudioData, 0, AudioData.length);

        AT.stop();
        AT.release();
    }

    public void Fast() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);
        int i;
        AT.setPlaybackRate(132300);
        AT.play();

        //short[] u = new short[save.getData().length];

        double[] data = save.getData();
        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }

        AT.write(AudioData, 0, AudioData.length);

        AT.stop();
        AT.release();
    }

    public void Fast2() {
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);
        int i;
        AT.setPlaybackRate(176400);
        AT.play();

        short[] u = new short[save.getData().length];

        double[] data = save.getData();
        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }

        AT.write(AudioData, 0, u.length);

        AT.stop();
        AT.release();
    }

    private Short Clipp (double raw){
        double ClipNum = (raw + 1.0) / 2.0 * 65536.0;
        if (ClipNum > 65535.0) {
            ClipNum = 65535.0;
        } else if (ClipNum < 0.0) {
            ClipNum = 0.0;
        }

        short data = (short)((ClipNum + 0.5) - 32768);
        return data;
    }

    double sinc(double x) {
        double y;
        if (x == 0.0) {
            y = 1.0;
        } else {
            y = Math.sin(x) / x;
        }
        return y;
    }

    public void initialize() {
        if(thread == null) {
            thread = new Thread(this);
        }
        playBufSize = android.media.AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AT = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                playBufSize, AudioTrack.MODE_STREAM);
    }

    public void run() {
        switch(MODE) {
            case "Normal":
                try {
                    Normal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Echo":
                try {
                    Echo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Vibrato":
                try{
                    Vibrato();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Tremolo":
                try{
                    Tremolo();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Radio":
                try{
                    Radio();
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case "Helium":
                try {
                    Helium();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Slow":
                try{
                    Slow();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Fast":
                try {
                    Fast();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "Electric":
                try{
                    Electric();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "sin2π":
                try{
                    sin2π();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case "sin5π":
                try{
                    sin5π();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    public void ThreadStart(String Mode) {
        if(Mode == this.MODE && AT.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            AT.stop();
            AT.release();
        } else if(this.MODE == ""){
            setMode(Mode);
            thread.start();
        } else if(AT.getPlayState() == AudioTrack.PLAYSTATE_STOPPED){
            setMode(Mode);
            thread.start();
        } else if(Mode != this.MODE && AT.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            AT.stop();
            AT.release();
            setMode(Mode);
            thread.start();
        }
    }
}
