package com.tech.sungkim.model;

/**
  Created by jacob on 6/12/2016.
 */

public class Qualification {

    private String commentUser;
    private String ratingUser;
    private Boolean share;

    public String getCommentUser() {
        return commentUser;
    }



    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public String getRatingUser() {
        return ratingUser;
    }

    public Boolean getShare() {
        return share;
    }

    public void setShare(Boolean share) {
        this.share = share;
    }

    public void setRatingUser(String ratingUser) {
        this.ratingUser = ratingUser;
    }
}
