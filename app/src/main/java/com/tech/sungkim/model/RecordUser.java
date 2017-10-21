package com.tech.sungkim.model;

/** Created by jacob on 27/09/2016.
 */

public class RecordUser {//Historial de sessions Usuario
    /***Datos para mostrar las sesiones*/
    private String idRecord;
    private String idChat;

    private String nroAttention;// numero de atenciones que realizo el usuario
    private Long startDate;//Fecha de inicio del chat
    private Long endDate;//Fecha final del chat
    private String nameProvider;
    private String nameUser;
    private String idUser;
    private String idProvider;

    //private String commentUser;//Optional
    private String commentProvider;
    //private String ratingUser;
    private String ratingProvider;

    private Qualification qualification;

    private String state; //estado de la teleconsulta.
    private String image;//imagen Provider


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public String getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(String idRecord) {
        this.idRecord = idRecord;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getCommentProvider() {
        return commentProvider;
    }

    public void setCommentProvider(String commentProvider) {
        this.commentProvider = commentProvider;
    }

    public String getRatingProvider() {
        return ratingProvider;
    }

    public void setRatingProvider(String ratingProvider) {
        this.ratingProvider = ratingProvider;
    }

    public String getNroAttention() {
        return nroAttention;
    }

    public void setNroAttention(String nroAttention) {
        this.nroAttention = nroAttention;
    }



    public String getNameProvider() {
        return nameProvider;
    }

    public void setNameProvider(String nameProvider) {
        this.nameProvider = nameProvider;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdProvider() {
        return idProvider;
    }

    public void setIdProvider(String idProvider) {
        this.idProvider = idProvider;
    }



    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
