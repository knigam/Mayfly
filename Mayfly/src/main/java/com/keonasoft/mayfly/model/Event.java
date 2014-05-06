package com.keonasoft.mayfly.model;

import android.content.Context;

import com.keonasoft.mayfly.helper.HttpHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

/**
 * Created by kushal on 4/12/14.
 */
public class Event {

    private int id;
    private String name;
    private String description;
    private String time;
    private String location;
    private Integer min;
    private Integer max;
    private Boolean attending;
    private Boolean creator;

    public Event() {}

    public Event(int id){
        this.id = id;
    }

    public Event(int id, String name, String description, String time, String location, Integer min, Integer max,
                 Boolean attending, Boolean creator){
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.location = location;
        this.min = min;
        this.max = max;
        this.attending = attending;
        this.creator = creator;
    }

    public Event getEvent(final String URI){
        JSONObject result = HttpHelper.httpGet(URI);
        try {
            name = result.getString("name");
            description = result.getString("description");
            time = result.getString("time");
            location = result.getString("location");
            min = result.getInt("min");
            max = result.getInt("max");
            attending = result.getBoolean("attending");
            creator = result.getBoolean("creator");

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Boolean getAttending() {
        return attending;
    }

    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

    public Boolean getCreator() {
        return creator;
    }

    public void setCreator(Boolean master) {
        this.creator = master;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", time=" + time +
                ", location='" + location + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", attending=" + attending +
                ", creator=" + creator +
                '}';
    }
}
