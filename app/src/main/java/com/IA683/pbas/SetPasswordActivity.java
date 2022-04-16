package com.IA683.pbas;

import static com.IA683.pbas.ExifHelper.copyExif;
import static com.IA683.pbas.ExifHelper.exifAttributesToString;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SetPasswordActivity extends AppCompatActivity {

    // Keeps track of number of taps
    private int pointCounter = 0;
    // Keeps track of current point
    private PointPair currentPoint = new PointPair();
    // Keeps track of all selected points
    private List<PointPair> pointList = new ArrayList<>();

    Button backButton;
    Button nextPointButton;
    Button doneButton;
    ImageView passwordImage;
    TextView pointText;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        backButton = (Button) findViewById(R.id.back_button);
        nextPointButton = (Button) findViewById(R.id.next_point_button);
        doneButton = (Button) findViewById(R.id.done_button);
        passwordImage = (ImageView) findViewById(R.id.set_password_image);
        pointText = (TextView) findViewById(R.id.point_text);

        doneButton.setEnabled(false);

        Bundle bmp = getIntent().getExtras();
        Uri selectedImage = (Uri) bmp.getParcelable("imageuri");

        passwordImage.setImageURI(selectedImage);

        updatePointText();

        nextPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentPoint.isCleared()) {
                    pointList.add(new PointPair(currentPoint.getX(), currentPoint.getY()));
                    pointCounter += 1;
                    updatePointText();
                    currentPoint.clear();
                }

                for (PointPair p : pointList) {
                    System.out.println(p.toString());
                }
            }
        });

        Intent backIntent = new Intent(this, SelectImageActivity.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pointCounter -= 1;
                if (pointCounter < 0) {
                    startActivity(backIntent);
                } else {
                    updatePointText();
                    pointList.remove(pointList.size() - 1);
                }
            }
        });

        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        passwordImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                System.out.println("X: " + x + " Y: " + y);
                currentPoint.setX(x);
                currentPoint.setY(y);
                // Vibrates for user feedback
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                return false;
            }
        });

        Intent doneIntent = new Intent(this, MainActivity.class);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serializePointData();
                saveImage(getApplicationContext(), selectedImage);

                startActivity(doneIntent);
            }
        });
    }

    private void updatePointText() {
        switch (pointCounter) {
            case 0: pointText.setText("Select First Point");
                    break;
            case 1: pointText.setText("Select Second Point");
                    break;
            case 2: pointText.setText("Select Third Point");
                    break;
            case 3: pointText.setText("Select Fourth Point");
                    break;
            case 4: pointText.setText("Select Fifth Point");
                    break;
            case 5: pointText.setText("Select Sixth Point");
                    break;
            case 6: pointText.setText("Maximum Amount of Points Selected");
                break;
        }

        if (pointCounter >= 3) {
            doneButton.setEnabled(true);
        }

        if (pointCounter > 5) {
            nextPointButton.setEnabled(false);
        }
    }

    public void saveImage(Context context, Uri selectedImage) {
        Bitmap bmp = null;

        try {
            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //FileOutputStream fileOutputStream;
        try {
            //fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            String path = getApplicationInfo().dataDir + "/img.jpg";
            FileOutputStream fos = new FileOutputStream(path, false);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            System.out.println(path);
            copyExif(PathUtil.getPath(context, selectedImage), path);
            System.out.println("OLD");
            exifAttributesToString(PathUtil.getPath(context, selectedImage));
            System.out.println("NEW");
            exifAttributesToString(path);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void serializePointData() {
        // To serialize pointList
        try
        {
            FileOutputStream fos = new FileOutputStream(getApplicationInfo().dataDir + "/point_data", false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pointList);
            oos.close();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}