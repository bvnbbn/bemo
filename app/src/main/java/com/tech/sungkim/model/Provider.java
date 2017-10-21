package com.tech.sungkim.model;


import java.io.Serializable;
import java.util.ArrayList;

public class Provider implements Serializable
{

    private String id; //Id del provedor
    private String fullName;
    private Long dateRegister;
    private String email;
    private String gender;
    private String image;
    private String idProvider;
    private String welcomeText;
    private String qualification1;
    private String qualify_2;
    private String Fcm_id;
    private ArrayList<String> episodes = new ArrayList<>();
    private String connection;
    private String year; //Experience
    private String specialty;
    private String lastTime;//last time connected   ---> ????????????
    private String description;//Breve informacion del proveedor
    private ChatAttention chatAttention;
    private String status;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public ArrayList<String> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<String> episodes) {
        this.episodes = episodes;
    }



    public String getWelcomeText() {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public String getQualification1() {
        return qualification1;
    }

    public void setQualification1(String qualification1) {
        this.qualification1 = qualification1;
    }

    public String getQualify_2() {
        return qualify_2;
    }

    public void setQualify_2(String qualify_2) {
        this.qualify_2 = qualify_2;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChatAttention getChatAttention() {
        return chatAttention;
    }

    public void setChatAttention(ChatAttention chatAttention) {
        this.chatAttention = chatAttention;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getFcm_id() {
        return Fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        Fcm_id = fcm_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(Long dateRegister) {
        this.dateRegister = dateRegister;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getimage() {
        return image;
    }

    public void setimage(String image) {
        this.image = image;
    }


    public String getIdProvider() {
        return idProvider;
    }

    public void setIdProvider(String idProvider) {
        this.idProvider = idProvider;
    }


}
