package com.example.voicechanger;


public class Window {
    public Window(){
    }

    public void Hanning_window(double[] w, int N) {
        //偶数の場合
        if(N % 2 == 0) {
            for(int n = 0; n < N; n++) {
                w[n] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * n / N);
            }
        } else {
            for(int n = 0; n < N; n++) {
                w[n] = 0.5 - 0.5 * Math.cos(2.0 * Math.PI * (n + 0.5) / N);
            }
        }
    }
}
