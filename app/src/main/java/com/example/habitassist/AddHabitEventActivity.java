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

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * The Android Activity that handles getting information about a new HabitEvent from the user, validating
 * it and then adding it to the database of habitsEvents.
 */

public class AddHabitEventActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Habit habitRecieved;
    private String imageBitmapStringToStore;

    private int CAMERA_REQUEST_CODE = 10;
    private int GALLERY_REQUEST_CODE = 12;
    private int LOCATION_REQUEST_CODE = 13;

    private ImageView imageView;
    private String latlngString;


    private Map mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit_event);

        db = FirebaseFirestore.getInstance();
        habitRecieved = (Habit) getIntent().getSerializableExtra("habit");
        imageView = (ImageView) findViewById(R.id.image_view);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        View mapViewWidget = findViewById(R.id.google_map);
        mapController = new Map(supportMapFragment, mapViewWidget);
    }

    /**
     * The on Click Gallery Method is called when the button to select photo from gallery is clicked.
     * it will open up your phone photos that you have had in the past and allow you to choose one.
     */

    public void onClickGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }


    /**
     * The onClickCamera method is called when you choose to take a picture it opens up the camera and
     * allows you to snap a photo and choose to use the photo, retake or cancel.
     */
    public void onClickCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * This Removes the picture from the HabitEvent.
     */
    public void onClickRemove(View view) {
        imageBitmapStringToStore = "";
        imageView.setVisibility(View.GONE);
        imageView.setImageBitmap(null);
    }

    /**
     * This saves the HabitEvent event in the Database when you click the save button.
     */
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

    /**
     * This allows you to choose to record the location of your new HabitEvent and save it
     * setting your location
     */

    public void locationButtonHandler(View view) {
        Intent intent = new Intent(AddHabitEventActivity.this, MapActivity.class);
        startActivityForResult(intent, LOCATION_REQUEST_CODE);
    }

    /**
     * This removes the location and sets your location to nothing
     */
    public void removeLocationButtonHandler(View view) {
        latlngString = null;
        mapController.hideMapWidget();
    }

    /**
     * This cancels the addition of a new Habit event and leaves this screen to return back to the Habit Detail screen.
     */
    public void onClickCancelButton(View view) {
        finish();
    }


    /**
     * TThis gets the results and information from the Camera and converts the picture data into a bitmap.
     * otherwise it gets the picture information from the gallery and converts to bitmap.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
                Bundle extra = data.getExtras();
                if (extra != null) {
                    Bitmap imageBitmap = (Bitmap) extra.get("data");
                    if (imageBitmap != null) {
                        imageView.setVisibility(View.VISIBLE);
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
                            imageView.setVisibility(View.VISIBLE);
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
                mapController.showOnMap(latlngString);
            }

        }
    }
}