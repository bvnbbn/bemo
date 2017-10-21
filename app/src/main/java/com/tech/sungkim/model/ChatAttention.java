package com.tech.sungkim.model;

import java.io.Serializable;

/** Created by jacob on 6/11/2016.
 */

public class ChatAttention implements Serializable{//para la sincronizacion, si esta en atencion => tenemos todos los datos para continuar(this jsonObject va en User data)

    private String idRecord;
    private String idProvider;
    private String idChat;
    private String idUser;
    private String state;//OPEN, CLOSE, PROCESSING,...

    private Long startTime; //Tiempo de inicio del chat
    private Long consultTime;



    public String getIdProvider() {
        return idProvider;
    }

    public void setIdProvider(String idProvider) {
        this.idProvider = idProvider;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(String idRecord) {
        this.idRecord = idRecord;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getConsultTime() {
        return consultTime;
    }

    public void setConsultTime(Long consultTime) {
        this.consultTime = consultTime;
    }
}
