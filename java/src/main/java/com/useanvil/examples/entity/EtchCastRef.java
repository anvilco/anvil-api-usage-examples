package com.useanvil.examples.entity;


public class EtchCastRef implements IAttachable {
    // Our ID we will use to reference and fill it with data.
    // It can be any string you want!
    public String id;

    // The id to the ready-made sample template. Fields and their ids are
    // specified when building out the template in the UI.
    public String castEid;

    public EtchCastRef(String id, String castEid) {
        this.id = id;
        this.castEid = castEid;
    }
}
