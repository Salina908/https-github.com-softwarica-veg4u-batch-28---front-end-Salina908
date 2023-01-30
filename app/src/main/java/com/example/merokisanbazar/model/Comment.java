package com.example.merokisanbazar.model;

import java.util.Date;

public class Comment {
    private String _id;
    private String text;
    private Date created;
    private String postedBy;

    public Comment(String text, Date created, String postedBy) {
        this.text = text;
        this.created = created;
        this.postedBy = postedBy;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
