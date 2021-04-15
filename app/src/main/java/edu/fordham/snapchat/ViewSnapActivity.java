package edu.fordham.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ViewSnapActivity extends AppCompatActivity {

    TextView messageTextView;
    ImageView snapImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        String imageUrl = getIntent().getStringExtra("url");
        String message = getIntent().getStringExtra("message");

        messageTextView = findViewById(R.id.messageTextView);
        snapImageView = findViewById(R.id.snapImageView);

        messageTextView.setText(message);
        Picasso.get()
                .load(imageUrl)
                .into(snapImageView);
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String key = getIntent().getStringExtra("id");
        String imageName = getIntent().getStringExtra("name");

        // Delete the viewed snap
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userSnapsRef = FirebaseDatabase.getInstance().getReference().child("snaps").child(user.getUid());
        userSnapsRef.child(key).removeValue();

        // Delete the image
        FirebaseStorage.getInstance().getReference().child("images").child(imageName).delete();
    }
}