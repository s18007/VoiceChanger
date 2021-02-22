package com.example.voicechanger;

import java.util.ArrayList;

public class SaveData {
    private static SaveData instance = new SaveData();

    private int DataLength; //データ長
    private int Rate; //サンプリング周波数
    private int Qa; //量子化精度
    private double Data[]; //音データ
    private int Count = 0;
    private int BufferSize = 0;
    private ArrayList<Short> list = new ArrayList<Short>();

    public static SaveData getInstance() {
        return instance;
    }
    public int getLength() {
        return DataLength;
    }
    public int getRate() {
        return Rate;
    }
    public int getQa() {
        return Qa;
    }
    //音データを取り出す処理

    public int getDataSize() {
        return this.Count * this.BufferSize;
    }

    public void setLength(int len) {
        this.DataLength = len;
    }
    public void setRate(int Rate) {
        this.Rate = Rate;
    }
    public void setQa(int Qa) {
        this.Qa = Qa;
    }

    public void setData(short data[]) {
        for(int i = 0; i < data.length; i++) {
            list.add(data[i]);
        }
    }

    public void ChangeType() {
        this.Data = new double[list.size()];
        for(int i = 0; i < this.list.size(); i++) {
            Data[i] = this.list.get(i) / 32768.0;
        }
    }

    public double[] getData(){
        return this.Data;
    }

}
