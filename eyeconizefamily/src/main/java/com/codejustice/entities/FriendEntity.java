package com.codejustice.entities;

import android.media.Image;

public class FriendEntity {
    public long friendID;

    public String friendName;
    public String lastMessage;
    public String phoneNumber;

    public long lastChangeProfileTime;

    public String profilePicLocation = "";
    public FriendEntity(String friendName, String lastMessage, String profilePicLocation) {
        this.friendName = friendName;
        this.lastMessage = lastMessage;
        this.profilePicLocation = profilePicLocation;
    }

    public FriendEntity(long friendID, String friendName, String phoneNumber, String profilePicLocation, long lastChangeProfileTime) {
        this.friendID = friendID;
        this.friendName = friendName;
        this.phoneNumber = phoneNumber;
        this.profilePicLocation = profilePicLocation;
        this.lastChangeProfileTime = lastChangeProfileTime;
    }

    public FriendEntity() {
    }

    public FriendEntity(String friendName, String lastMessage) {
        this.friendName = friendName;
        this.lastMessage = lastMessage;
    }

}
