package com.friends.friendschat;

public class Messages {
    private String from,message,type,receiverTo,messageID,time,date,name;

    public  Messages()
    {

    }

    public Messages(String from, String message, String type, String receiverTo, String messageID, String time, String date, String name) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.receiverTo = receiverTo;
        this.messageID = messageID;
        this.time = time;
        this.date = date;
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getReceiverTo() {
        return receiverTo;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }
}
