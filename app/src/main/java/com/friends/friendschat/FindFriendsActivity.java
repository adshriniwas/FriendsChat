package com.friends.friendschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private EditText search_et;
    private ImageButton search_btn;
    private DatabaseReference UserRef,rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef = FirebaseDatabase.getInstance().getReference();

        search_et = findViewById(R.id.search_et);
        search_btn = findViewById(R.id.search_btn);
        FindFriendsRecyclerList = findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");



    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null)
        {
            search_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String searchText = search_et.getText().toString();
                    searchFor(searchText);
                }
            });

            FirebaseRecyclerOptions<Contacts> options =
                    new FirebaseRecyclerOptions.Builder<Contacts>()
                            .setQuery(UserRef, Contacts.class)
                            .build();

            FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {

                            holder.userName.setText(model.getUname());
                            holder.userStatus.setText(model.getStatus());
                            Picasso.get().load(model.getProfileimage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String Visit_user_id = getRef(position).getKey();
                                    Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("Visit_user_id", Visit_user_id);
                                    startActivity(profileIntent);
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                            FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                            return viewHolder;
                        }
                    };
            adapter.startListening();

            FindFriendsRecyclerList.setAdapter(adapter);



        }

    }



    private void updateUserStatus(String state)
    {
        String saveCurrentTime,saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time",saveCurrentTime);
        onlineState.put("date", saveCurrentDate);
        onlineState.put("state",state);

        currentUserID = mAuth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineState);



    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName,userStatus;
        CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentUser != null)
        {
            updateUserStatus("online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    private void searchFor(String searchText) {


            Query firebaseSearchQuery = UserRef.orderByChild("Uname").startAt(searchText).endAt(searchText + "\uf8ff");



            FirebaseRecyclerOptions<Contacts> options =
                    new FirebaseRecyclerOptions.Builder<Contacts>()
                            .setQuery(firebaseSearchQuery, Contacts.class)
                            .build();

            FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contacts model) {

                            holder.userName.setText(model.getUname());
                            holder.userStatus.setText(model.getStatus());
                            Picasso.get().load(model.getProfileimage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String Visit_user_id = getRef(position).getKey();
                                    Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("Visit_user_id", Visit_user_id);
                                    startActivity(profileIntent);
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                            FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                            return viewHolder;
                        }
                    };
            adapter.startListening();

            FindFriendsRecyclerList.setAdapter(adapter);



    }

}
