package com.example.voicechanger;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WavSave {
    Window window = new Window();
    SaveData save = new SaveData();
    double depth = 1.0;
    double rate = 150.0;
    long RECORDER_BPP = 16;
    long RECORDER_SAMPLERATE = 44100;
    public WavSave(){
    }

    private void Normal() throws Exception {

        double[] data = save.getData();
        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }

    }

    private void Echo() {

        int k = 0;
        double a = 0.5; //減衰率
        double d = save.getRate() * 0.375; //遅延時間
        int repeat = 2; //繰り返し回数

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


    }


    public void Robot() throws IOException {
        FileOutputStream out;
        BufferedOutputStream bos;
        DataOutputStream dos;
        String FileName = getFileName();
        try {
            File file = new File(FileName);
            out = new FileOutputStream(file);
            bos = new BufferedOutputStream(out);
            dos = new DataOutputStream(bos);

            long totalAudioLen = 0;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = RECORDER_SAMPLERATE;
            int channels = 2;
            long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;
            WriteWaveFileHeader(dos, totalAudioLen,
                    totalDataLen, longSampleRate, channels,
                    byteRate);


            //short[] AudioData = new short[save.getData().length];
            double[] data = save.getData();
            System.out.println("WavSave.java: 87: " + data.length);
            double[] effect = new double[data.length];
            for (int t = 0; t < data.length; t++) {
                double a = depth * Math.sin(2.0 * Math.PI * rate * t / save.getRate());
                effect[t] = a * data[t];
                dos.writeShort(Clipp(effect[t]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Radio() {


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
                y[n] = Clipp(effect[n]);
            }
        }
    }
    //ビブラート処理
    public void Vibrato() {

        //wavのヘッダーを書き込む

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
    }

    //コーラス
    public void Chorus() {

        //wavのヘッダーを書き込む
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
            //データをファイルに書き込む
        }

    }

    //トレモロ
    public void Tremolo() {

        //wavのヘッダーを書き込む
        short[] AudioData = new short[save.getData().length];

        double[] data = save.getData();

        for(int n = 0; n < data.length; n++) {
            double a = 1.0 + depth * Math.sin(2.0 * Math.PI * rate * n / save.getRate());
            AudioData[n] = Clipp(a * data[n]);
        }
    }

    //フランジャ
    public void Helium() {

        //wavのヘッダーを書き込む
        short[] AudioData = new short[save.getData().length];
        double[] data = save.getData();
        double[] img = new double[data.length];
        FFT fft = new FFT();
        int frame_size = 512;
        double[] win_data = new double[frame_size];
        window.Hanning_window(win_data, frame_size);
        double[][] test = fft.STFT(data, data.length, win_data, frame_size);
        for(int n = 1; n <= 3; n++) {
            for(int k = 0; k < frame_size; k++){
                System.out.println("フレーム " + n  + "番目" + "周波数"+ k +"の振幅: " + data[k]);
            }
        }
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

    public void Slow() {

        //wavのヘッダーを書き込む
        int i;

        save.ChangeType();
        double[] data = save.getData();

        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }

    }

    public void Fast() {

        int i;
        //wavのヘッダーを書き込む

        double[] data = save.getData();
        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }

    }

    public void Fast2() {
        //wavのヘッダーを書き込む

        short[] u = new short[save.getData().length];

        double[] data = save.getData();
        short[] AudioData = new short[data.length];
        for(int y = 0; y < data.length; y++){
            AudioData[y] = Clipp(data[y]);
        }
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


    private String getFileName() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File Folder = new File(filepath,"VoiceChanger/RecordFile");

        if (!Folder.exists()) {
            Folder.mkdirs();
        }
        String FileName = System.currentTimeMillis() + ".wav";
        File file = new File(Folder.getAbsolutePath(), FileName);
        return (file.getAbsolutePath());
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
        header[34] = 16; // bits per sample
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
    }

}
