package com.codejustice.entities;

import android.media.Image;

public class FamilyEntity {
    public String familyName;
    public String lastMessage;

    public FamilyEntity(String familyName, String lastMessage, Image image) {
        this.familyName = familyName;
        this.lastMessage = lastMessage;
        this.image = image;
    }

    public FamilyEntity() {
    }

    public FamilyEntity(String familyName, String lastMessage) {
        this.familyName = familyName;
        this.lastMessage = lastMessage;
    }

    public Image image = null;
    }
