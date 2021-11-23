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

    private ImageView imageView;

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

        commentEditText.setText(habitEventRecieved.getComment());
        if (imageBitmapStringToStore != null && !imageBitmapStringToStore.isEmpty()) {
            Bitmap imageBitmap = HabitEvent.stringToBitMap(imageBitmapStringToStore);
            imageView.setImageBitmap(imageBitmap);
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
            String title = habitEventRecieved.getHabitTitle();
            String timeStamp = habitEventRecieved.getTimeStamp();
            HabitEvent habitEvent = new HabitEvent(title, timeStamp, comment, imageBitmapStringToStore);
            String uniqueHabitEventID = title + "*" + timeStamp;
            HashMap<String, String> habitEventDocument = habitEvent.getDocument();
            db.collection("habitEvents")
                    .document(uniqueHabitEventID)
                    .set(habitEventDocument)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(HabitEventEditActivity.this, "IT WAS WRITTEN", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HabitEventEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error writing document", e);
                        }
                    });;
            finish();
        }
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
                        int y = imageBitmap.getHeight();
                        int x = imageBitmap.getWidth();
                        Toast.makeText(this, "h, w: "+ String.valueOf(y) + ", " + String.valueOf(x), Toast.LENGTH_SHORT).show();
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
                        int y = imageBitmap.getHeight();
                        int x = imageBitmap.getWidth();
                        Toast.makeText(this, "h, w: "+ String.valueOf(y) + ", " + String.valueOf(x), Toast.LENGTH_SHORT).show();
                        imageBitmapStringToStore = HabitEvent.bitMapToString(imageBitmap, true);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // private void updateHabitEventRecieved() {
    //     DocumentReference docRef = db.collection("habitEvents").document(habitEventRecieved.getUniqueId());
    //     docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    //         @Override
    //         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
    //             if (task.isSuccessful()) {
    //                 DocumentSnapshot document = task.getResult();
    //                 if (document.exists()) {
    //                     // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
    //                     Map<String, Object> data = document.getData();
    //                     String comment = (String) data.get("comment");
    //                     String habitTitle = (String) data.get("habitTitle");
    //                     String timeStamp = (String) data.get("timeStamp");
    //                     String imageBitmapString = (String) data.get("imageBitmapString");
    //                     if (habitTitle != null && timeStamp != null) {
    //                         habitEventRecieved = new HabitEvent(habitTitle, timeStamp, comment, imageBitmapString);
    //
    //                         // Fill the comment using habitEventRecieved
    //                         commentEditText.setText(habitEventRecieved.getComment());
    //
    //                         // Fill the photo using habitEventRecieved
    //                         if (imageBitmapString != null && !imageBitmapString.isEmpty()) {
    //                             Bitmap imageBitmap = HabitEvent.stringToBitMap(imageBitmapString);
    //                             imageView.setImageBitmap(imageBitmap);
    //                             imageBitmapStringToStore = imageBitmapString;
    //                         }
    //                     }
    //                 } else {
    //                     Log.d(TAG, "No such document");
    //                 }
    //             } else {
    //                 Log.d(TAG, "get failed with ", task.getException());
    //             }
    //         }
    //     });
    // }
}