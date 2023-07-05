package com.codejustice.entities;

import android.media.Image;

public class FriendEntity implements Comparable{
    public long friendID;

    public String friendName;
    public String lastMessage = "";
    public long lastMessageTime = 0;
    public String phoneNumber = "";

    public long lastChangeProfileTime = 0;

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

    public FriendEntity(String friendName, String lastMessage, long friendID) {
        this.friendName = friendName;
        this.lastMessage = lastMessage;
        this.friendID = friendID;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof FriendEntity)) {
            return -1;
        }
        return -(int)(this.lastMessageTime-((FriendEntity) o).lastMessageTime);
    }
}
