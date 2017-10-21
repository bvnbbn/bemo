package com.tech.sungkim.model;

import java.io.Serializable;

/**
 * Created by vikas on 20/6/17.
 */

public class SupportTeam implements Serializable
{

    private String support_id;
    private String support_name;
    private String support_fcm_id;
    private String support_email;

    public String getSupport_id() {
        return support_id;
    }

    public void setSupport_id(String support_id) {
        this.support_id = support_id;
    }

    public String getSupport_name() {
        return support_name;
    }

    public void setSupport_name(String support_name) {
        this.support_name = support_name;
    }

    public String getSupport_fcm_id() {
        return support_fcm_id;
    }

    public void setSupport_fcm_id(String support_fcm_id) {
        this.support_fcm_id = support_fcm_id;
    }

    public String getSupport_email() {
        return support_email;
    }

    public void setSupport_email(String support_email) {
        this.support_email = support_email;
    }
}
