package com.useanvil.examples.entity;

import java.io.Serializable;

public class CreateEtchPacket implements Serializable  {
    // Indicate the packet is all ready to send to the
    // signers. An email will be sent to the first signer.
    public boolean isDraft;

    // Test packets will use development signatures and
    // not count toward your billed packets
    public boolean isTest;

    // Subject & body of the emails to signers
    public String name;
    public String signatureEmailSubject;
    public String signatureEmailBody;

    // Merge all PDFs into one PDF before signing.
    // Signing users will get one PDF instead of all PDFs as separate files.
    public boolean mergePDFs;

    public IAttachable[] files;

    public PayloadData data;
    public EtchSigner[] signers;
}
