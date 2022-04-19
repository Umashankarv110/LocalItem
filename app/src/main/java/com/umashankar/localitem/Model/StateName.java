package com.umashankar.localitem.Model;

public class StateName {
    int id;
    String stateName;

    public StateName() {
    }

    public StateName(String stateName) {
        this.stateName = stateName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public String toString() {
        return this.getStateName(); // What to display in the Spinner list.
    }
}
