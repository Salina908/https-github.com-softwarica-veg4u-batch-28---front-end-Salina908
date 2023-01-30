package com.example.merokisanbazar.model;

import android.graphics.drawable.Drawable;

public class Category {
    private String _id;
    private String name;
    private Drawable icon;


    public Category(String _id, String name) {
        this._id = _id;
        this.name = name;
    }

    public Category(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    public Category(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
