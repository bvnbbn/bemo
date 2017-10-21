package com.tech.sungkim.model;

/**
 * Created by HP-PC on 03-04-2017.
 */

public class Episode {
    private int counsellor;
    private boolean paid;
    private String problem;
    private String user;

    public Episode(int counsellor, boolean paid, String problem, String user) {
        this.counsellor = counsellor;
        this.paid = paid;
        this.problem = problem;
        this.user = user;
    }

    public int isCounsellor() {
        return counsellor;
    }

    public void setCounsellor(int counsellor) {
        this.counsellor = counsellor;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
