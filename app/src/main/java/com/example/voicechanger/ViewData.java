package com.example.voicechanger;

public class ViewData {
    private int image_name;
    private String text;
    public ViewData(int image, String text) {
        this.image_name = image;
        this.text = text;
    }
    public int getImage_name() {
        return this.image_name;
    }
    public void setImage_name(int image_name) {
        this.image_name = image_name;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
