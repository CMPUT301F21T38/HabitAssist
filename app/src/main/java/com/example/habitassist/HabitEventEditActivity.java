package com.example.habitassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HabitEventEditActivity extends AppCompatActivity {
    FirebaseFirestore db;
    HabitEvent habitEventRecieved;

    EditText commentEditText;

    private String imageBitmapStringToStore;

    private int CAMERA_REQUEST_CODE = 10;
    private int GALLERY_REQUEST_CODE = 12;
    private int LOCATION_REQUEST_CODE = 13;

    private ImageView imageView;
    private String latlngString;

    private String TAG = "HabitEventEditActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_event_edit);
        commentEditText = (EditText) findViewById(R.id.comment_edit_text);
        imageView = (ImageView) findViewById(R.id.image_view);

        db = FirebaseFirestore.getInstance();
        habitEventRecieved = (HabitEvent) getIntent().getSerializableExtra("habitEventPassed");
        imageBitmapStringToStore = habitEventRecieved.getImageBitmapString();
        latlngString = habitEventRecieved.getLatlngString();

        commentEditText.setText(habitEventRecieved.getComment());
        if (imageBitmapStringToStore != null && !imageBitmapStringToStore.isEmpty()) {
            Bitmap imageBitmap = HabitEvent.stringToBitMap(imageBitmapStringToStore);
            imageView.setImageBitmap(imageBitmap);
        }
        if (habitEventRecieved.getLatlngString() != null && !habitEventRecieved.getLatlngString().isEmpty()) {
            ((TextView) findViewById(R.id.latlng)).setText("latitude, longitude: " + latlngString);
        }
    }

    public void onClickGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void onClickCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onClickRemove(View view) {
        imageBitmapStringToStore = "";
        imageView.setImageBitmap(null);
    }


    public void onClickSaveButton(View view) {
        String comment = commentEditText.getText().toString();
        if (comment.length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the comment under 20 characters", Toast.LENGTH_SHORT).show();
        } else {
            String timeStamp = habitEventRecieved.getTimeStamp();
            String parentHabitUniqueId =  habitEventRecieved.getParentHabitUniqueId();
            HabitEvent habitEvent = new HabitEvent(parentHabitUniqueId, timeStamp, comment,
                    imageBitmapStringToStore, latlngString);
            db.collection("habitEvents")
                    .document(habitEvent.getUniqueId()).set(habitEvent.getDocument());
            finish();
        }
    }


    public void locationButtonHandler(View view) {
        Intent intent = new Intent(HabitEventEditActivity.this, MapActivity.class);
        startActivityForResult(intent, LOCATION_REQUEST_CODE);
    }

    public void onClickCancelButton(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
                Bundle extra = data.getExtras();
                if (extra != null) {
                    Bitmap imageBitmap = (Bitmap) extra.get("data");
                    if (imageBitmap != null) {
                        imageView.setImageBitmap(imageBitmap);
                        imageBitmapStringToStore = HabitEvent.bitMapToString(imageBitmap, true);

                    }
                }
            }

        } else if (requestCode == GALLERY_REQUEST_CODE) {
            try {
                if (data != null) {
                    Uri imageURI = data.getData();
                    if (imageURI != null) {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                        imageView.setImageBitmap(imageBitmap);
                        imageBitmapStringToStore = HabitEvent.bitMapToString(imageBitmap, true);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
        if (data != null) {
            latlngString = data.getStringExtra("latlngString");
            ((TextView) findViewById(R.id.latlng)).setText("latitude, longitude: " + latlngString);
        }

    }
    }
}