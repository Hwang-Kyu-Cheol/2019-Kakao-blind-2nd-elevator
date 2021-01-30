package com.company.json.response;

import com.company.domain.Elevator;

import java.util.ArrayList;
import java.util.List;

public class Start {

    private String token;
    private int timestamp;
    private List<Elevator> elevators = new ArrayList<>();
    private boolean is_end;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    public void setElevators(List<Elevator> elevators) {
        this.elevators = elevators;
    }

    public boolean isIs_end() {
        return is_end;
    }

    public void setIs_end(boolean is_end) {
        this.is_end = is_end;
    }
}
