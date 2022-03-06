package com.frostdev.kutako.Model;

public class Comment {

    private String comment, date, postId, publisher;

    public Comment(String comment, String date, String postId, String publisher) {
        this.comment = comment;
        this.date = date;
        this.postId = postId;
        this.publisher = publisher;
    }

    public Comment() {
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


}
