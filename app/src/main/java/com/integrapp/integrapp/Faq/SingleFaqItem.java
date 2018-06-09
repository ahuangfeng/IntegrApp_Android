package com.integrapp.integrapp.Faq;

public class SingleFaqItem {

    private String title;
    private String content;

    public SingleFaqItem(){}

    public SingleFaqItem(String title, String content) {
        this.title = title;
        this.content = content;
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
}
