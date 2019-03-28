package com.gui.gui.news_feed;

import java.io.Serializable;

/***
 * This contains all the individual elements to be stored in database,
 * as well as to be read from the API response and helps to displaying data to user
 *
 * Serializable is implemented to pass instance of this class with the INTENT
 * */
public class NFNewsData implements Serializable {
    private String uuid;
    private String url;
    private String title;
    private String text;
    private String published;
    private String author;
    private String language;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
