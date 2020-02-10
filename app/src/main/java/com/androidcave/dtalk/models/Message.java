package com.androidcave.dtalk.models;

import java.util.Calendar;

public class Message {

  private   String messageId, senderUID,receiverUID,txtMessage,msgStatus;
  private String timeDateSent;

    public Message(String messageID, String senderUID, String receiverUID, String txtMessage, String msgStatus, String timeDateSent) {
        this.messageId=messageID;
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
        this.txtMessage = txtMessage;
        this.msgStatus = msgStatus;
        this.timeDateSent = timeDateSent;
    }

    public Message() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public String getTxtMessage() {
        return txtMessage;
    }

    public void setTxtMessage(String txtMessage) {
        this.txtMessage = txtMessage;
    }

    public String getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getTimeDateSent() {
        return timeDateSent;
    }

    public void setTimeDateSent(String timeDateSent) {
        this.timeDateSent = timeDateSent;
    }
}
