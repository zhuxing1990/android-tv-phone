package com.vunke.videochat.model;

public class TalkBean {
    private String talkTime;
    private long talkDuration;
    private String userId;

    public String getTalkTime() {
        return talkTime;
    }

    public void setTalkTime(String talkTime) {
        this.talkTime = talkTime;
    }

    public long getTalkDuration() {
        return talkDuration;
    }

    public void setTalkDuration(long talkDuration) {
        this.talkDuration = talkDuration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TalkBean{" +
                "talkTime='" + talkTime + '\'' +
                ", talkDuration='" + talkDuration + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
