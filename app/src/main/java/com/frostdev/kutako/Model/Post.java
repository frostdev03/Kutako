package com.frostdev.kutako.Model;

public class Post {

    private String askedBy, data, postId, publisher, question, questionimage, topic;

    public Post() {
    }

    public Post(String askedBy, String data, String postId, String publisher, String question, String questionimage, String topic) {
        this.askedBy = askedBy;
        this.data = data;
        this.postId = postId;
        this.publisher = publisher;
        this.question = question;
        this.questionimage = questionimage;
        this.topic = topic;
    }

    public String getAskedBy() {
        return askedBy;
    }

    public void setAskedBy(String askedBy) {
        this.askedBy = askedBy;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionimage() {
        return questionimage;
    }

    public void setQuestionimage(String questionimage) {
        this.questionimage = questionimage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
