package com.example.habitassist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddHabitEventActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Habit habitRecieved;
    private String imageBitmapStringToStore;

    private int CAMERA_REQUEST_CODE = 10;
    private int GALLERY_REQUEST_CODE = 12;
    private int LOCATION_REQUEST_CODE = 13;

    private ImageView imageView;
    private String latlngString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit_event);

        db = FirebaseFirestore.getInstance();
        habitRecieved = (Habit) getIntent().getSerializableExtra("habit");
        imageView = (ImageView) findViewById(R.id.image_view);
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
        String comment = ((EditText) findViewById(R.id.comment_edit_text)).getText().toString();
        if (comment.length() > 20) {
            Toast.makeText(getApplicationContext(), "Please keep the comment under 20 characters", Toast.LENGTH_SHORT).show();
        } else {
            // Added it to firebase
            // Source: https://stackoverflow.com/a/23068721/8270982
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String parentHabitUniqueId = habitRecieved.getUniqueId();
            HabitEvent habitEvent = new HabitEvent(parentHabitUniqueId, timeStamp, comment,
                    imageBitmapStringToStore, latlngString);
            db.collection("habitEvents")
                    .document(habitEvent.getUniqueId()).set(habitEvent.getDocument());
            finish();
        }
    }

    public void locationButtonHandler(View view) {
        Intent intent = new Intent(AddHabitEventActivity.this, MapActivity.class);
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
                        InputStream inputStream;
                        inputStream = getContentResolver().openInputStream(imageURI);
                        if (inputStream != null) {
                            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                            imageView.setImageBitmap(imageBitmap);
                            imageBitmapStringToStore = HabitEvent.bitMapToString(imageBitmap, true);
                        }
                    }
                }
            } catch (Exception e) {
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