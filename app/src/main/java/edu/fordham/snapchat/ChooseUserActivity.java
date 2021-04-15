package edu.fordham.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseUserActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<User, UserViewHolder> firebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("uri");
        String message = intent.getStringExtra("message");
        String imageName = intent.getStringExtra("name");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Snap snap = new Snap(imageName, currentUser.getEmail(), imageUrl, message);

        RecyclerView userList = findViewById(R.id.userList);

        // Initialize Realtime Database
        DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseDatabaseRef.child("users"), User.class)
                        .build();

        firebaseAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User user) {
                holder.emailTextView.setText(user.getEmail());
                holder.emailTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("mobdev", "User clicked!");
                        DatabaseReference snapRef = FirebaseDatabase.getInstance().getReference().child("snaps").child(user.getUid()).push();
                        snap.setId(snapRef.getKey());
                        snapRef.setValue(snap);

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            }
        };

        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(firebaseAdapter);
    }

    @Override
    public void onStop() {
        firebaseAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAdapter.startListening();
    }

}