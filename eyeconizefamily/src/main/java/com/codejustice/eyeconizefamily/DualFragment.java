package com.codejustice.eyeconizefamily;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DualFragment extends Fragment {

    private FrameLayout familyPickerFragment;
    public FrameLayout chatFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dual, container, false);

        // 获取 FamilyPickerFragment 的 FrameLayout
        familyPickerFragment = rootView.findViewById(R.id.familyPickerFragment);

        // 获取 ChatFragment 的 FrameLayout
        chatFragment = rootView.findViewById(R.id.chatFragment);


        return rootView;
    }

    // 在适当的时机调用下面的方法来替换 FamilyPickerFragment 和 ChatFragment

    public void replaceFamilyPickerFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.familyPickerFragment, fragment);
        transaction.commit();
    }

    public void replaceChatFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.chatFragment, fragment);
        transaction.commit();
    }

}
