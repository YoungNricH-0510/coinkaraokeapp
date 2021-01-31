package com.holy.coinkaraoke.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {

    int id;
    String userId;
    String title;
    String message;
    String youtubeUrl;
    LocalDateTime uploadTime;
    int likes;
    List<String> likedUsers;

    public Post(int id, String userId, String title, String message, String youtubeUrl,
                LocalDateTime uploadTime, int likes, List<String> likedUsers) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.youtubeUrl = youtubeUrl;
        this.uploadTime = uploadTime;
        this.likes = likes;
        if (likedUsers != null) {
            this.likedUsers = likedUsers;
        } else {
            this.likedUsers = new ArrayList<>();
        }
    }

    public Post(String userId, String title, String message, String youtubeUrl,
                LocalDateTime uploadTime, int likes, List<String> likedUsers) {
        this.id = -1;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.youtubeUrl = youtubeUrl;
        this.uploadTime = uploadTime;
        this.likes = likes;
        if (likedUsers != null) {
            this.likedUsers = likedUsers;
        } else {
            this.likedUsers = new ArrayList<>();
        }
    }

    public int getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public int getLikes() {
        return likes;
    }

    public List<String> getLikedUsers() {
        return likedUsers;
    }

}
