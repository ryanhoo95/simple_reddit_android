package com.example.simplereddit.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Topic implements Serializable {
    private String id;
    private String deviceId;
    private String topic;
    private String upVote;
    private String downVote;

    public Topic() {
        topic = "dwajdwa";
        upVote = "0";
        downVote = "0";
    }

    public Topic(JSONObject data) {
        // id
        try {
            id = data.getString("_id");
        } catch (JSONException e) {
            id = "";
        }

        // device id
        try {
            deviceId = data.getString("deviceId");
        } catch (JSONException e) {
            deviceId = "";
        }

        // topic
        try {
            topic = data.getString("topic");
        } catch (JSONException e) {
            topic = "";
        }

        // upvote
        try {
            upVote = data.getString("upvote");
        } catch (JSONException e) {
            upVote = "";
        }

        // downvote
        try {
            downVote = data.getString("downvote");
        } catch (JSONException e) {
            downVote = "";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUpVote() {
        return upVote;
    }

    public void setUpVote(String upVote) {
        this.upVote = upVote;
    }

    public String getDownVote() {
        return downVote;
    }

    public void setDownVote(String downVote) {
        this.downVote = downVote;
    }

    public void update(Topic topic) {
        id = topic.getId();
        deviceId = topic.getDeviceId();
        this.topic = topic.getTopic();
        upVote = topic.getUpVote();
        downVote = topic.getDownVote();
    }
}
