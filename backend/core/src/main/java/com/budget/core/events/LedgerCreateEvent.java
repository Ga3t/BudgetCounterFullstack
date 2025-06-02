package com.budget.core.events;

public class LedgerCreateEvent {
    private String userId;

    public LedgerCreateEvent(){}

    public LedgerCreateEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}