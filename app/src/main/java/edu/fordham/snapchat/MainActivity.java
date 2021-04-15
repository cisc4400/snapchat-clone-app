package edu.fordham.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter<Snap, UserViewHolder> firebaseAdapter;
    private RecyclerView snapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snapList = findViewById(R.id.snapList);
        snapList.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            createSignInIntent();
        } else {
            resetAdapter(user);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = firebaseAuth.getCurrentUser();
                resetAdapter(user);
                if (response.isNewUser())
                    FirebaseDatabase.getInstance().getReference().child("users")
                            .child(user.getUid()).setValue(new User(user.getEmail(), user.getUid()));
            } else {
                Toast.makeText(this, "Sign in failed: " + response.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOutMenuItem) {
            signOut();
            return true;
        } else if (item.getItemId() == R.id.createSnap) {
            Intent intent = new Intent(this, CreateSnapActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop() {
        if (firebaseAdapter != null)
            firebaseAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAdapter != null)
            firebaseAdapter.startListening();
    }

    void resetAdapter(FirebaseUser user) {
        Log.i("mobdev", "Current user: " + user.getEmail() + ", " + user.getUid());
        DatabaseReference userSnapsRef = FirebaseDatabase.getInstance().getReference().child("snaps").child(user.getUid());
        FirebaseRecyclerOptions<Snap> options =
                new FirebaseRecyclerOptions.Builder<Snap>()
                        .setQuery(userSnapsRef, Snap.class)
                        .build();
        firebaseAdapter = new FirebaseRecyclerAdapter<Snap, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Snap snap) {
                holder.emailTextView.setText(snap.getFrom());
                holder.emailTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("mobdev", "User clicked!");
                        Intent i = new Intent(getApplicationContext(), ViewSnapActivity.class);
                        i.putExtra("message", snap.getMessage());
                        i.putExtra("name", snap.getName());
                        i.putExtra("url", snap.getUrl());
                        i.putExtra("id", snap.getId());
                        startActivity(i);
                    }
                });
            }
        };
        snapList.setAdapter(firebaseAdapter);
        firebaseAdapter.startListening();
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        createSignInIntent();
                    }
                });
    }

}