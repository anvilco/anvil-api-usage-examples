package com.useanvil.examples.entity.request;

import java.io.Serializable;

public class SignerField implements Serializable {
    public String fileId;
    public String fieldId;

    public SignerField(String fileId, String fieldId) {
        this.fileId = fileId;
        this.fieldId = fieldId;
    }
}
