package mmu.edu.my.traco_19.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import mmu.edu.my.traco_19.Activities.UserDashboard;
import mmu.edu.my.traco_19.Dialogs.BottomSheetDialog;
import mmu.edu.my.traco_19.R;

public class Profile extends Fragment implements PopupMenu.OnMenuItemClickListener {

    //views
    TextView name, email, status, profileName, profileStatus;
    ImageButton nameB, emailB, statusB;
    LinearLayout PreviewMode;
    ImageView selectedImage;
    ProgressBar progressBar;
    Switch serviceSwitch,locationSwitch;

    //vars
    String Id;
    Context context;
    UserDashboard userDashboard;
    boolean init = false;
    boolean popUp = true;
    BottomSheetDialog bottomSheet;
    Profile profile = this;

    public Profile(String id, Context context, UserDashboard userDashboard) {
        this.Id = id;
        this.context = context;
        this.userDashboard = userDashboard;
    }

    public void setURI(Uri uri) {
        if (selectedImage != null) {
            selectedImage.setImageURI(uri);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!Id.isEmpty() && getView() != null) {
            InitFunc();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void InitFunc() {
        init = true;
        selectedImage = Objects.requireNonNull(getView()).findViewById(R.id.profilePictureAdmin);
        profileName = getView().findViewById(R.id.userName);
        profileStatus = getView().findViewById(R.id.userStatus);
        status = getView().findViewById(R.id.Status);
        name = getView().findViewById(R.id.Name);
        email = getView().findViewById(R.id.Email);
        PreviewMode = getView().findViewById(R.id.PreviewMode);
        nameB = getView().findViewById(R.id.nameB);
        emailB = getView().findViewById(R.id.emailB);
        statusB = getView().findViewById(R.id.statusB);
        serviceSwitch = getView().findViewById(R.id.serviceSwitch);
        locationSwitch=getView().findViewById(R.id.locationSwitch);
        progressBar = getView().findViewById(R.id.progressBar);
        updateInfo();
        emailB.setOnClickListener(v -> createBottomSheet(email.getText().toString(), "Enter your email", "email"));
        nameB.setOnClickListener(v -> createBottomSheet(name.getText().toString(), "Enter your name", "name"));
        statusB.setOnClickListener(v -> showPopUp(status));
        serviceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userDashboard.turnOnService(isChecked,serviceSwitch));
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> userDashboard.showLocation(isChecked));
    }

    public void updateInfo() {
        FirebaseDatabase.getInstance().getReference().child("Member").child(Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists() && dataSnapshot.child("email").exists() && dataSnapshot.child("status").exists()) {
                    userDashboard.DownloadProfilePic(selectedImage);
                    profileName.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    name.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    email.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                    int status = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString());
                    setButtonStatus(status);
                    serviceSwitch.setChecked(userDashboard.isMyServiceRunning());
                    locationSwitch.setChecked(userDashboard.getShowLocation());
                }else{
                    Toast.makeText(context,"Your info doesn't exist on our server!",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void createBottomSheet(String content, String title, String val) {
        bottomSheet = new BottomSheetDialog();
        bottomSheet.setProfile(profile);
        bottomSheet.setId(Id);
        bottomSheet.setValString(content);
        bottomSheet.setTitle(title);
        bottomSheet.setDataName(val);
        if (getFragmentManager() != null) {
            bottomSheet.show(getFragmentManager(), "exampleBottomSheet");
        }
    }

    public void showPopUp(View view) {
        popUp = true;
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_pop_up);
        popup.show();
    }

    @SuppressLint("SetTextI18n")
    public void setButtonStatus(int statusInt) {
        if (statusInt == 1) {
            profileStatus.setText("Healthy");
            profileStatus.setTextColor(Color.GREEN);
            status.setText("Healthy");
        } else if (statusInt == 2) {
            profileStatus.setText("Suspicious case");
            profileStatus.setTextColor(Color.parseColor("#FFCE00"));
            status.setText("Suspicious case");
        } else if (statusInt == 3) {
            profileStatus.setText("Infected with corona");
            profileStatus.setTextColor(Color.RED);
            status.setText("Infected with corona");
        }
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (popUp) {
            switch (item.getItemId()) {
                case R.id.item1:
                    FirebaseDatabase.getInstance().getReference().child("Member").child(Id).child("status").setValue("1");
                    status.setText("Healthy");
                    return true;
                case R.id.item2:
                    FirebaseDatabase.getInstance().getReference().child("Member").child(Id).child("status").setValue("2");
                    status.setText("suspicious case");
                    return true;
                case R.id.item3:
                    FirebaseDatabase.getInstance().getReference().child("Member").child(Id).child("status").setValue("3");
                    status.setText("Infected with corona");
                    return true;
                default:
                    return false;
            }
        }
        return true;
    }

    public void cancel() {
        bottomSheet.dismiss();
    }
}