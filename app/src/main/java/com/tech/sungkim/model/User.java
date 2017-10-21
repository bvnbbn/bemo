package com.tech.sungkim.model;


import java.io.Serializable;
import java.util.List;

public class User implements Serializable{

    private String id;
    private String email;
    private String name;
    private int age;
    private String photo;
    private String gender;
    private long score;
    private List<String> episodes;
    private int AggregateQuestion;
    private int Question1;
    private int Question2;
    private ChatAttention chatAttention;

    public User(){

    }



    public ChatAttention getChatAttention() {
        return chatAttention;
    }

    public void setChatAttention(ChatAttention chatAttention) {
        this.chatAttention = chatAttention;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }



    private String status;//AVAILABLE, UNAVAILABLE



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public List<String> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<String> episodes) {
        this.episodes = episodes;
    }

    public int getAggregateQuestion() {
        return AggregateQuestion;
    }

    public void setAggregateQuestion(int aggregateQuestion) {
        AggregateQuestion = aggregateQuestion;
    }

    public int getQuestion1() {
        return Question1;
    }

    public void setQuestion1(int question1) {
        Question1 = question1;
    }

    public int getQuestion2() {
        return Question2;
    }

    public void setQuestion2(int question2) {
        Question2 = question2;
    }
}
