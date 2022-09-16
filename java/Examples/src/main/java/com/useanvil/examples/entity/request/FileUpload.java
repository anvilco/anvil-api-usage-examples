package com.useanvil.examples.entity.request;


import java.io.Serializable;

public class FileUpload implements IAttachable {

    public String id;
    public String title;
    public Serializable file;
    public CastField[] fields;


    public FileUpload(String id, String title, Serializable file, CastField[] fields) {
        this.id = id;
        this.title = title;
        this.file = file;
        this.fields = fields;
    }
}
