package com.example.simplereddit.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplereddit.model.Topic;
import com.example.simplereddit.utils.DeviceUtils;

import java.util.ArrayList;

public class TopicViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Topic>> topics;

    public LiveData<ArrayList<Topic>> getTopics() {
        if (topics == null) {
            topics = new MutableLiveData<>();
        }

        return topics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        if (this.topics == null) {
            this.topics = new MutableLiveData<>();
        }
        this.topics.setValue(topics);
    }

    public void findCurrentUserTopicAndSaveCache(Context context, ArrayList<Topic> topics) {
        ArrayList<Topic> currentUserTopics = new ArrayList<>();

        // filter
        for (Topic topic : topics) {
            if (topic.getDeviceId().equals(DeviceUtils.getDeviceId(context))) {
                currentUserTopics.add(topic);
            }
        }

        // add to cache if any
        if (!currentUserTopics.isEmpty()) {
            setTopics(currentUserTopics);
        }
    }

    public void updateTopic(Topic topic) {
        if (topics == null) {
            topics = new MutableLiveData<>();
        }

        if (topics.getValue() == null) {
            // no cache yet, create new one
            ArrayList<Topic> newTopics = new ArrayList<>();
            newTopics.add(topic);
            setTopics(newTopics);
        } else {
            // update the cache list
            ArrayList<Topic> cacheTopics = topics.getValue();

            boolean isUpdated = false;

            for (int i = 0; i < cacheTopics.size(); i++) {
                if (cacheTopics.get(i).getId().equals(topic.getId())) {
                    cacheTopics.get(i).update(topic);
                    isUpdated = true;
                    break;
                }
            }

            if (!isUpdated) {
                cacheTopics.add(topic);
            }

            setTopics(cacheTopics);
        }
    }
}
