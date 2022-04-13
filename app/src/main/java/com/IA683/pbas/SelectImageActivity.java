package com.IA683.pbas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SelectImageActivity extends AppCompatActivity {

    Button selectImageButton;
    Button cancelButton;
    Button nextButton;
    ImageView passwordImageView;

    // Constant to compare the activity result code
    int SELECT_PICTURE = 200;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        selectImageButton = (Button) findViewById(R.id.select_image_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setEnabled(false);
        passwordImageView = (ImageView) findViewById(R.id.password_image_select);

        // Handle the Choose Image button to trigger the image chooser function
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        Intent cancelIntent = new Intent(this, MainActivity.class);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(cancelIntent);
            }
        });

        Intent nextIntent = new Intent(this, SetPasswordActivity.class);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putParcelable("imageuri", selectedImageUri);
                nextIntent.putExtras(extras);
                startActivity(nextIntent);
            }
        });
    }

    // This function is triggered when the Select Image Button is clicked
    void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // Pass the constant to compare it with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // This function is triggered when user selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // Compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Update the preview image in the layout
                    passwordImageView.setImageURI(selectedImageUri);
                    nextButton.setEnabled(true);
                }
            }
        }
    }
}