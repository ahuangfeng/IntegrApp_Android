package com.integrapp.integrapp.Model;

import android.graphics.drawable.Drawable;

public class Faq {

    private String title;
    private String content;
    private Drawable image;

    public Faq(){}

    public Faq(String title, String content, Drawable image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
