package com.example.voicechanger;

import java.math.*;

public class FFT {
    private String STATUS = "";
    public FFT() {
    }

    public void fft(double[] x_real, double[] x_imag, int N) {
        int n, m, r;
        int[] index = new int[N];
        double a_real, a_imag, b_real, b_imag, c_real, c_imag, real, imag;
        int number_of_stage = log2(N);
        if(number_of_stage == 0) {
            STATUS = "ERR";
        } else {
            STATUS = "SUC";
            //バタフライ演算
            for (int stage = 1; stage <= number_of_stage; stage++) {
                for (int i = 0; i < pow2(stage-1); i++) {
                    for(int j = 0; j < pow2(number_of_stage); j++) {
                        n = pow2(number_of_stage - stage + 1) * i + j;
                        m = pow2(number_of_stage - stage) + n;
                        r = pow2(stage - 1) * j;
                        a_real = x_real[n];
                        a_imag = x_imag[n];
                        b_real = x_real[m];
                        b_imag = x_imag[m];
                        c_real = Math.cos((2.0 * Math.PI * r) / N);
                        c_imag = -Math.sin((2.0 * Math.PI * r) / N);
                        if(stage < number_of_stage) {
                            x_real[n] = a_real + b_real;
                            x_imag[n] = a_imag + b_imag;
                            x_real[m] = (a_real - b_real) * c_real - (a_imag - b_imag) * c_imag;
                            x_imag[m] = (a_imag - b_imag) * c_real + (a_real - b_real) * c_imag;
                        } else {
                            x_real[n] = a_real + b_real;
                            x_imag[n] = a_imag + b_imag;
                            x_real[m] = a_real - b_real;
                            x_imag[m] = a_imag - b_imag;
                        }
                    }
                }
            }
            for(int stage = 1; stage <= number_of_stage; stage++) {
                for(int i = 0; i < pow2(stage - 1); i++) {
                    index[pow2(stage - 1) + i] = index[i] + pow2(number_of_stage - stage);
                }
            }

            for(int k = 0; k < N; k++) {
                if(index[k] > k) {
                    real = x_real[index[k]];
                    imag = x_imag[index[k]];
                    x_real[index[k]] = x_real[k];
                    x_imag[index[k]] = x_imag[k];
                    x_real[k] = real;
                    x_imag[k] = imag;
                }
            }
        }
    }

    public void IFFT(double[] x_real, double[] x_imag, int N) {
        int n, m, r;
        double a_real, a_imag, b_real, b_imag, c_real, c_imag, real, imag;

        int number_of_stage = log2(N);

        /* バタフライ計算 */
        for (int stage = 1; stage <= number_of_stage; stage++) {
            for (int i = 0; i < pow2(stage - 1); i++) {
                for (int j = 0; j < pow2(number_of_stage - stage); j++) {
                    n = pow2(number_of_stage - stage + 1) * i + j;
                    m = pow2(number_of_stage - stage) + n;
                    r = pow2(stage - 1) * j;
                    a_real = x_real[n];
                    a_imag = x_imag[n];
                    b_real = x_real[m];
                    b_imag = x_imag[m];
                    c_real = Math.cos((2.0 * Math.PI * r) / N);
                    c_imag = Math.sin((2.0 * Math.PI * r) / N);
                    if (stage < number_of_stage) {
                        x_real[n] = a_real + b_real;
                        x_imag[n] = a_imag + b_imag;
                        x_real[m] = (a_real - b_real) * c_real - (a_imag - b_imag) * c_imag;
                        x_imag[m] = (a_imag - b_imag) * c_real + (a_real - b_real) * c_imag;
                    } else {
                        x_real[n] = a_real + b_real;
                        x_imag[n] = a_imag + b_imag;
                        x_real[m] = a_real - b_real;
                        x_imag[m] = a_imag - b_imag;
                    }
                }
            }
        }
        int[] index = new int[N];
        for (int stage = 1; stage <= number_of_stage; stage++) {
            for (int i = 0; i < pow2(stage - 1); i++) {
                index[pow2(stage - 1) + i] = index[i] + pow2(number_of_stage - stage);
            }
        }

        /* インデックスの並び替え */
        for (int k = 0; k < N; k++) {
            if (index[k] > k)
            {
                real = x_real[index[k]];
                imag = x_imag[index[k]];
                x_real[index[k]] = x_real[k];
                x_imag[index[k]] = x_imag[k];
                x_real[k] = real;
                x_imag[k] = imag;
            }
        }

        /* 計算結果をNで割る */
        for (int k = 0; k < N; k++) {
            x_real[k] /= N;
            x_imag[k] /= N;
        }
    }

    public void DFT(double[] x_real, double[] x_imag, int N) {
        for(int k = 0; k < N; k++) {
            for(int n = 0; n < N; n++) {
                double W_real = Math.cos(2.0 * Math.PI * k * n / N);
                double W_imag = -Math.sin(2.0 * Math.PI * k * n / N);
                x_real[k] += W_real * x_real[n] - W_imag * x_imag[n];
                x_imag[k] += W_real * x_imag[n] + W_imag * x_real[n];
            }
        }
    }
    public double[][] STFT(double[] data, int N, double[] win, int frame_size) {
        double[] x_real = new double[frame_size];
        double[] x_imag = new double[frame_size];
        //double[][] AlterData= new double[][];
        int split_num = (N - frame_size) / (frame_size / 2) + 1;
        double[][] result = new double[split_num][frame_size];
        int count = 0;
        for(int offset = 0; offset < N - frame_size; offset+=frame_size/2) {
            for(int k = 0; k < frame_size; k++) {
                x_real[k] = data[k] * win[k];
                fft(x_real, x_imag, frame_size);
                for(int n = 0; n < x_real.length; n++) {
                    x_real[n] = x_real[n] * Math.cos(3 * n); //cosωt
                }

                result[count] = x_real;
                //data[k] = Math.log10(Math.pow(Math.abs(x_real[k]), 2));
            }
            count++;
        }

        return result;
    }

    /*
    public double[] ISTFT(double[][] data, int N, double[] win, int frame_size){

    }
    */

    public int log2(int n) {
        double deno = Math.log10(2);
        double Nume = Math.log10(n);
        if(Nume%deno == 0.0) {
            return (int)(Nume / deno);
        }
        return 0;
    }

    public int pow2(int n) {
        int result = 0;
        for(int i = 1; i <= n; i++) {
            result *= 2;
        }
        return result;
    }
}
