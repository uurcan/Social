package com.example.social.model;

public class Messages {
    private String sender;
    private String receiver;
    private String message;

    public Messages(String sender,String receiver,String message){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getSender() {
        return sender;
    }
    public String getReceiver(){
        return receiver;
    }
    public void setReceiver(String receiver){
        this.receiver = receiver;
    }
}
