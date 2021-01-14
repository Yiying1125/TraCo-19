package mmu.edu.my.traco_19.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import mmu.edu.my.traco_19.Activities.UserDashboard;
import mmu.edu.my.traco_19.R;

public class Profile extends Fragment {
    UserDashboard userDashboard;
    Context context;
    String Id="";

    public Profile(String id, Context context,UserDashboard userDashboard) {
        this.Id = id;
        this.context = context;
        this.userDashboard = userDashboard;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

}