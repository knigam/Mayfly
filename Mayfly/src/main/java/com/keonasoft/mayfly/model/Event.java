package com.keonasoft.mayfly.model;

import android.content.Context;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.helper.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kushal on 4/12/14.
 */
public class Event {

    private int id;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private String location;
    private Integer min;
    private Integer max;
    private Boolean attending;
    private Boolean creator;
    private Map<Integer, String> usersAttending;

    public Event() {}

    public Event(int id){
        this.id = id;
    }

    public Event(int id, String name, String description, String startTime, String endTime, String location, Integer min, Integer max,
                 Boolean attending, Boolean creator){
        this.id = id;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.min = min;
        this.max = max;
        this.attending = attending;
        this.creator = creator;
    }

    public Event getEvent(final String URI){
        JSONObject result = null;
        usersAttending = new HashMap<Integer, String>();
        try {
            result = HttpHelper.httpGet(URI);
        } catch (Exception e) {
            return null;
        }
        try {
            name = result.getString("name");
            description = result.getString("description");
            startTime = result.getString("start_time");
            endTime = result.getString("end_time");
            location = result.getString("location");
            min = result.getInt("min");
            max = result.getInt("max");
            attending = result.getBoolean("attending");
            creator = result.getBoolean("creator");

            //Get the list of users attending
            JSONArray users = result.getJSONArray("users_attending");
            for(int i = 0; i < users.length(); i++){
                JSONObject user = users.getJSONObject(i);
                Integer id = user.getInt("id");
                String name = user.getString("name");
                usersAttending.put(id, name);
            }

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String time) {
        this.startTime = time;
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

    public Map<Integer, String> getUsersAttending() {
        return usersAttending;
    }

    public void setUsersAttending(Map<Integer, String> usersAttending) {
        this.usersAttending = usersAttending;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", location='" + location + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", attending=" + attending +
                ", creator=" + creator +
                ", usersAttending=" + usersAttending +
                '}';
    }
}
