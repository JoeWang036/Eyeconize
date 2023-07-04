package com.codejustice.vo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.codejustice.eyeconizefamily.R;

public class FriendVO {

    public String friendName;
    public String lastMessage;
    public Drawable profilePic;
    public long lastMessageTime;
    public int unreadCount;

    public FriendVO(String friendName, String lastMessage, Drawable profilePic, long lastMessageTime, int unreadCount) {
        this.friendName = friendName;
        this.lastMessage = lastMessage;
        this.profilePic = profilePic;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public FriendVO(String friendName, String lastMessage, String profilePicLocation, long lastMessageTime, int unreadCount, Context context) {
        this.friendName = friendName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;

        if (profilePicLocation != null && !profilePicLocation.isEmpty()) {
            // 文件位置不为空时，尝试从文件系统中读取图像
            Bitmap bitmap = BitmapFactory.decodeFile(profilePicLocation);
            if (bitmap != null) {
                this.profilePic = bitmapToDrawable(bitmap, context);
            } else {
                // 文件位置无效或无法解码图像时，设置默认图像
                this.profilePic = getDefaultProfilePic(context);
            }
        } else {
            // 文件位置为空时，设置默认图像
            this.profilePic = getDefaultProfilePic(context);
        }
    }

    private Drawable bitmapToDrawable(Bitmap bitmap, Context context) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getDefaultProfilePic(Context context) {
        // 返回默认图像，例如通过资源ID获取
        return context.getResources().getDrawable(R.drawable.ic_launcher_background, null);
    }
}
