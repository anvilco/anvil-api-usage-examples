package com.useanvil.examples.entity;

import java.io.Serializable;

public class EtchSigner implements Serializable {
    public String id;
    public String name;
    public String email;
    public String signerType;
    public SignerField[] fields;
}
