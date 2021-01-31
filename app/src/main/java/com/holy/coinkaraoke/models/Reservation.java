package com.holy.coinkaraoke.models;

import java.time.LocalDateTime;

public class Reservation {

    private final int id;
    private final int roomNumber;
    private final LocalDateTime beginTime;
    private final LocalDateTime endTime;
    private final String userId;

    public Reservation(int roomNumber, LocalDateTime beginTime, LocalDateTime endTime, String userId) {
        this.id = -1;
        this.roomNumber = roomNumber;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.userId = userId;
    }

    public Reservation(int id, int roomNumber, LocalDateTime beginTime, LocalDateTime endTime, String userId) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.userId = userId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getUserId() {
        return userId;
    }


    public boolean conflictsWith(LocalDateTime time) {

        return (time.isAfter(beginTime) && time.isBefore(endTime));
    }
}
