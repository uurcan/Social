package com.example.social.model.messaging;

public class Messages {
    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;

    public Messages(){}
    public Messages(String sender,String receiver,String message,boolean isSeen){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
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
