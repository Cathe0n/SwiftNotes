package org.geeksforgeeks.simple_notes_application_java;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String tag;
    private String content;
    private int color; // store as ARGB int

    public Note(String title, String tag, String content, int color) {
        this.title = title;
        this.tag = tag;
        this.content = content;
        this.color = color;
    }

    public String getTitle() { return title; }
    public String getTag() { return tag; }
    public String getContent() { return content; }
    public int getColor() { return color; }

    public void setTitle(String title) { this.title = title; }
    public void setTag(String tag) { this.tag = tag; }
    public void setContent(String content) { this.content = content; }
    public void setColor(int color) { this.color = color; }
}
