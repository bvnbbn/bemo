package com.tech.sungkim.model;


import android.graphics.Bitmap;

import java.io.File;

public class MessageChat
{

    private String message;
    private String receive;
    private String sender;
    private Boolean visto;
    private String name;
    private Long timewrite;
    private Boolean hasAttachment;
    private Boolean attachment;
    private Bitmap localBitmap;
    private String uri;
    private boolean isLocal;
    private String fileName;
    private int attachmentType;
    private String receive_therapist;
    private String image_url;


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getReceive_therapist() {
        return receive_therapist;
    }

    public void setReceive_therapist(String receive_therapist) {
        this.receive_therapist = receive_therapist;
    }

    public Boolean getAttachment() {
        return attachment;
    }

    public void setAttachment(Boolean attachment) {
        this.attachment = attachment;
    }



    public Bitmap getLocalBitmap() {
        return localBitmap;
    }

    public void setLocalBitmap(Bitmap localBitmap) {
        this.localBitmap = localBitmap;
    }

    private int senderOrRecipient;

    public Boolean getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uriString) {
        this.uri = uriString;
    }

    public int getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(int attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Boolean getVisto() {
        return visto;
    }

    public void setVisto(Boolean visto) {
        this.visto = visto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimewrite() {
        return timewrite;
    }

    public void setTimewrite(Long timewrite) {
        this.timewrite = timewrite;
    }

    public int getSenderOrRecipient() {
        return senderOrRecipient;
    }

    public void setSenderOrRecipient(int senderOrRecipient)
    {
        this.senderOrRecipient = senderOrRecipient;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
