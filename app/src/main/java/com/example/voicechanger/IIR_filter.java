package com.example.voicechanger;

public class IIR_filter {
    private double A[];
    private double B[];

    public IIR_filter(){}

    public double[] getA() {
        return A;
    }
    public double[] getB() {
        return B;
    }

    public void IIR_LPF(double fc, double Q, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = 4.0 * Math.PI * Math.PI * fc * fc / a[0];
        b[1] = 8.0 * Math.PI * Math.PI * fc * fc / a[0];
        b[2] = 4.0 * Math.PI * Math.PI * fc * fc / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_HPF(double fc, double Q, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = 1.0 / a[0];
        b[1] = -2.0 / a[0];
        b[2] = 1.0 / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_BPF(double fc1, double fc2, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc1 = Math.tan(Math.PI * fc1) / (2.0 * Math.PI);
        fc2 = Math.tan(Math.PI * fc2) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * (fc2 - fc1) + 4.0 * Math.PI * Math.PI * fc1 * fc2;
        a[1] = (8.0 * Math.PI * Math.PI * fc1 * fc2 - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * (fc2 - fc1) + 4.0 * Math.PI * Math.PI * fc1 * fc2) / a[0];
        b[0] = 2.0 * Math.PI * (fc2 - fc1) / a[0];
        b[1] = 0.0;
        b[2] = -2.0 * Math.PI * (fc2 - fc1) / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_BEF(double fc1, double fc2, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc1 = Math.tan(Math.PI * fc1) / (2.0 * Math.PI);
        fc2 = Math.tan(Math.PI * fc2) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * (fc2 - fc1) + 4.0 * Math.PI * Math.PI * fc1 * fc2;
        a[1] = (8.0 * Math.PI * Math.PI * fc1 * fc2 - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * (fc2 - fc1) + 4.0 * Math.PI * Math.PI * fc1 * fc2) / a[0];
        b[0] = (4.0 * Math.PI * Math.PI * fc1 * fc2 + 1.0) / a[0];
        b[1] = (8.0 * Math.PI * Math.PI * fc1 * fc2 - 2.0) / a[0];
        b[2] = (4.0 * Math.PI * Math.PI * fc1 * fc2 + 1.0) / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_resonator(double fc, double Q, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = 2.0 * Math.PI * fc / Q / a[0];
        b[1] = 0.0;
        b[2] = -2.0 * Math.PI * fc / Q / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_notch(double fc, double Q, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = (4.0 * Math.PI * Math.PI * fc * fc + 1.0) / a[0];
        b[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        b[2] = (4.0 * Math.PI * Math.PI * fc * fc + 1.0) / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_low_shelving(double fc, double Q, double g, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = (1.0 + Math.sqrt(1.0 + g) * 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc * (1.0 + g)) / a[0];
        b[1] = (8.0 * Math.PI * Math.PI * fc * fc * (1.0 + g) - 2.0) / a[0];
        b[2] = (1.0 - Math.sqrt(1.0 + g) * 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc * (1.0 + g)) / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_high_shelving(double fc, double Q, double g, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = ((1.0 + g) + Math.sqrt(1.0 + g) * 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0 * (1.0 + g)) / a[0];
        b[2] = ((1.0 + g) - Math.sqrt(1.0 + g) * 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }

    public void IIR_peaking(double fc, double Q, double g, int a_length, int b_length) {
        double[] a = new double[a_length];
        double[] b = new double[b_length];

        fc = Math.tan(Math.PI * fc) / (2.0 * Math.PI);

        a[0] = 1.0 + 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc;
        a[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        a[2] = (1.0 - 2.0 * Math.PI * fc / Q + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[0] = (1.0 + 2.0 * Math.PI * fc / Q * (1.0 + g) + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        b[1] = (8.0 * Math.PI * Math.PI * fc * fc - 2.0) / a[0];
        b[2] = (1.0 - 2.0 * Math.PI * fc / Q * (1.0 + g) + 4.0 * Math.PI * Math.PI * fc * fc) / a[0];
        a[0] = 1.0;

        this.A = a;
        this.B = b;
    }
}


