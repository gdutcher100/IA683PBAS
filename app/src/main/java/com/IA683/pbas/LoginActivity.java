package com.IA683.pbas;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private final double WIDTH_THRESHOLD = .1;
    private final double HEIGHT_THRESHOLD = .1;

    private List<PointPair> loginPointList = new ArrayList<>();
    private List<PointPair> pointList = new ArrayList<>();

    private PointPair currentPoint = new PointPair();
    private int pointCounter = 0;
    private boolean started = false;
    private boolean restart = false;

    Button backButton;
    Button startButton;
    Button loginButton;
    TextView pointText;
    ImageView passwordImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backButton = (Button) findViewById(R.id.login_back_button);
        startButton = (Button) findViewById(R.id.login_start_button);
        loginButton = (Button) findViewById(R.id.login_login_button);
        pointText = (TextView) findViewById(R.id.login_point_text);
        passwordImage = (ImageView) findViewById(R.id.login_password_image);

        loginButton.setEnabled(false);

        deserializePointData();
        passwordImage.setImageURI(Uri.fromFile(new File(getApplicationInfo().dataDir + "/img.jpg")));

        Intent backIntent = new Intent(this, MainActivity.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(backIntent);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restart) {
                    if (started) {
                        if (!currentPoint.isCleared()) {
                            pointList.add(new PointPair(currentPoint.getX(), currentPoint.getY()));
                            pointCounter += 1;
                            updatePointText();
                            currentPoint.clear();
                        }

                        for (PointPair p : pointList) {
                            System.out.println(p.toString());
                        }
                    } else {
                        started = true;
                        startButton.setText("Next Point");
                        updatePointText();
                    }
                } else {
                    startActivity(getIntent());
                }
            }
        });

        Intent loginIntent = new Intent(this, LoggedInActivity.class);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restart) {
                    if (comparePointLists(pointList, loginPointList, .1)) {
                        startActivity(loginIntent);
                    } else {
                        startButton.setText("Restart");
                        pointText.setText("Invalid Password");
                        restart = true;
                    }
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
    }

    private void deserializePointData() {
        try
        {
            FileInputStream fis = new FileInputStream(getApplicationInfo().dataDir + "/point_data");
            ObjectInputStream ois = new ObjectInputStream(fis);

            loginPointList = (ArrayList) ois.readObject();

            for (PointPair p : loginPointList) {
                System.out.println(p.toString());
            }

            ois.close();
            fis.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }
        catch (ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
    }

    private boolean comparePointLists(List<PointPair> originalPointList, List<PointPair> newPointList, double threshold) {
        if (originalPointList.size() == (newPointList.size())) {
            for (int i = 0; i < originalPointList.size(); i++) {

                long lowXRange = (long) (originalPointList.get(i).getX() - (threshold * passwordImage.getWidth()));
                long lowYRange = (long) (originalPointList.get(i).getY() - (threshold * passwordImage.getHeight()));
                long highXRange = (long) (originalPointList.get(i).getX() + (threshold * passwordImage.getWidth()));
                long highYRange = (long) (originalPointList.get(i).getY() + (threshold * passwordImage.getHeight()));

                if (!(ValueRange.of(lowXRange, highXRange).isValidIntValue(newPointList.get(i).getX()) &&
                        ValueRange.of(lowYRange, highYRange).isValidIntValue(newPointList.get(i).getY()))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    private void updatePointText() {
        switch (pointCounter) {
            case 0:
                pointText.setText("Select First Point");
                break;
            case 1:
                pointText.setText("Select Second Point");
                break;
            case 2:
                pointText.setText("Select Third Point");
                break;
            case 3:
                pointText.setText("Select Fourth Point");
                break;
            case 4:
                pointText.setText("Select Fifth Point");
                break;
            case 5:
                pointText.setText("Select Sixth Point");
                break;
            case 6:
                pointText.setText("Maximum Amount of Points Selected");
                break;
        }

        if (pointCounter >= 3) {
            loginButton.setEnabled(true);
        }

        if (pointCounter > 5) {
            startButton.setEnabled(false);
        }
    }
}