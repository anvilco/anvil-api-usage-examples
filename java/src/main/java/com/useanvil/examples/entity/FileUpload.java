package com.useanvil.examples.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.nio.file.Path;

public class FileUpload implements IAttachable {

    public String id;
    public String title;
    //
    public Serializable file;
    public CastField[] fields;

    @JsonIgnore
    Path _file;


    /**
     * Constructor for when `file` is a string. This means the file is a
     * base64-encoded string (or should be) and we will send that directly into
     * the payload.
     * @param id
     * @param title
     * @param file
     * @param fields
     */
    public FileUpload(String id, String title, Serializable file, CastField[] fields) {
        this.id = id;
        this.title = title;
        this.file = file;
        this.fields = fields;
    }


    /**
     * Constructor for when `file` is a Path. This will set the file to be
     * sent through a multipart request process.
     *
     * Note that the `file` parameter in this case will be set as `this._file`
     * because we don't want this serialized directly with jackson.
     *
     * the payload.
     * @param id
     * @param title
     * @param file
     * @param fields
     */
    public FileUpload(String id, String title, Path file, CastField[] fields) {
        this.id = id;
        this.title = title;
        this._file = file;
        this.fields = fields;
    }

    @JsonIgnore
    public Path getFilePath() {
        return this._file;
    }
}
