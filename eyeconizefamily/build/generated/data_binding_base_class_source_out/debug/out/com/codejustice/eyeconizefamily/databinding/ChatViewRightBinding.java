// Generated by view binder compiler. Do not edit!
package com.codejustice.eyeconizefamily.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.codejustice.eyeconizefamily.R;
import com.makeramen.roundedimageview.RoundedImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ChatViewRightBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final RoundedImageView chatProfilePic;

  @NonNull
  public final TextView messageContent;

  private ChatViewRightBinding(@NonNull ConstraintLayout rootView,
      @NonNull RoundedImageView chatProfilePic, @NonNull TextView messageContent) {
    this.rootView = rootView;
    this.chatProfilePic = chatProfilePic;
    this.messageContent = messageContent;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ChatViewRightBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ChatViewRightBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.chat_view_right, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ChatViewRightBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.chat_profile_pic;
      RoundedImageView chatProfilePic = ViewBindings.findChildViewById(rootView, id);
      if (chatProfilePic == null) {
        break missingId;
      }

      id = R.id.messageContent;
      TextView messageContent = ViewBindings.findChildViewById(rootView, id);
      if (messageContent == null) {
        break missingId;
      }

      return new ChatViewRightBinding((ConstraintLayout) rootView, chatProfilePic, messageContent);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}