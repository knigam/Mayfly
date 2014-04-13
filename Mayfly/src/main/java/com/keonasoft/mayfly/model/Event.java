package com.keonasoft.mayfly.model;

import java.sql.Time;

/**
 * Created by kushal on 4/12/14.
 */
public class Event {

    private int id;
    private String name;
    private String description;
    private Time time;
    private String location;
    private Integer min;
    private Integer max;
    private Boolean attending;
    private Boolean master;

    public Event() {}

    public Event(int id, String name, String description, Time time, String location, Integer min, Integer max,
                 Boolean attending, Boolean master){
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.location = location;
        this.min = min;
        this.max = max;
        this.attending = attending;
        this.master = master;
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

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
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

    public Boolean getMaster() {
        return master;
    }

    public void setMaster(Boolean master) {
        this.master = master;
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
                ", master=" + master +
                '}';
    }
}
