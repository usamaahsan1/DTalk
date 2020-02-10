package com.androidcave.dtalk.models;

import java.util.HashMap;

public class Status {
    private String posterId;
    private String statusId,statusImgUrl,statusText;
    private int totalLikes;
    private HashMap<String,String> likedBy;

    public Status(String statusId, String statusImgUrl, String statusText, int totalLikes, HashMap<String,String> likedBy, String posterId) {
        this.statusId = statusId;
        this.statusImgUrl = statusImgUrl;
        this.statusText = statusText;
        this.totalLikes = totalLikes;
        this.likedBy = likedBy;
        this.posterId = posterId;
    }





    public Status() {
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }


    public HashMap<String, String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(HashMap<String, String> likedBy) {
        this.likedBy = likedBy;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusImgUrl() {
        return statusImgUrl;
    }

    public void setStatusImgUrl(String statusImgUrl) {
        this.statusImgUrl = statusImgUrl;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }
}
