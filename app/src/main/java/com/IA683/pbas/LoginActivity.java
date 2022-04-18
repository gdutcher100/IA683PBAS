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

import com.IA683.pbas.Helpers.PointPair;
import com.IA683.pbas.Helpers.TouchTimer;
import com.IA683.pbas.Helpers.VibrationTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private final double POINT_THRESHOLD = .1;
    private final long TIME_THRESHOLD_MILLISECONDS = 500;

    private List<PointPair> loginPointList = new ArrayList<>();
    private List<PointPair> pointList = new ArrayList<>();
    private List<PointPair> currentPointListAverage = new ArrayList<>();
    private List<VibrationTracker> vibrations = new ArrayList<>();
    private List<VibrationTracker> taps = new ArrayList<>();

    private PointPair currentPoint = new PointPair();
    private int pointCounter = 0;
    private boolean started = false;
    private boolean restart = false;
    private boolean isVibrating = false;
    private boolean isLongPress = false;
    private int tapAmount = 0;
    private long pressTime = 0;

    Button backButton;
    Button startButton;
    Button loginButton;
    Button enterButton;
    TextView pointText;
    ImageView passwordImage;
    View tapView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backButton = (Button) findViewById(R.id.login_back_button);
        startButton = (Button) findViewById(R.id.login_start_button);
        loginButton = (Button) findViewById(R.id.login_login_button);
        enterButton = (Button) findViewById(R.id.login_enter_button);
        pointText = (TextView) findViewById(R.id.login_point_text);
        passwordImage = (ImageView) findViewById(R.id.login_password_image);
        tapView = (View) findViewById(R.id.login_tap_view);

        loginButton.setEnabled(false);
        enterButton.setEnabled(false);

        deserializePointData();
        passwordImage.setImageURI(Uri.fromFile(new File(getApplicationInfo().dataDir + "/img.jpg")));

        Intent backIntent = new Intent(this, MainActivity.class);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(backIntent);
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVibrating) {
                    if (started) {
                        System.out.println("HERE");
                        enterButton.setEnabled(false);
                        if (!isLongPress) {
                            pointList.add(calculatePointPairAverage(currentPointListAverage));
                            currentPointListAverage.clear();
                            taps.add(new VibrationTracker(false, tapAmount, 0));
                        } else {
                            taps.add(new VibrationTracker(true, 0, pressTime));
                            System.out.println("X: " + currentPoint.getX());
                            pointList.add(new PointPair(currentPoint.getX(), currentPoint.getY()));
                        }

                        tapAmount = 0;
                        pointCounter += 1;
                        updatePointText();

                        for (PointPair p : pointList) {
                            System.out.println(p.toString());
                        }
                        startButton.setEnabled(true);
                    }
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (restart) {
                    startActivity(getIntent());
                } else {
                    startButton.setEnabled(false);
                    try {
                        createLoginVibrations();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    started = true;
                    startButton.setText("Next Point");
                    updatePointText();
                    enterButton.setEnabled(true);
                }
            }
        });

        Intent loginIntent = new Intent(this, LoggedInActivity.class);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!restart && !isVibrating) {
                    if (comparePointLists(pointList, loginPointList, POINT_THRESHOLD) && compareVibrationLists(vibrations, taps, TIME_THRESHOLD_MILLISECONDS)) {
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
                if (!isLongPress)
                    currentPointListAverage.add(new PointPair(x, y));
                else {
                    currentPoint.setX(x);
                    currentPoint.setY(y);
                }
                tapAmount += 1;

                // Vibrates for user feedback
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                return false;
            }
        });

        tapView.setOnTouchListener(new TouchTimer() {
            @Override
            protected void onTouchEnded(long touchTimeInMillis) {
                System.out.println("TouchTime: " + touchTimeInMillis);
                pressTime = touchTimeInMillis;
/*                if (isLongPress) {
                    taps.add(new VibrationTracker(true, 0, touchTimeInMillis));
                }*/
            }
        });
    }

    private void deserializePointData() {
        try
        {
            FileInputStream fis = new FileInputStream(getApplicationInfo().dataDir + "/point_data");
            ObjectInputStream ois = new ObjectInputStream(fis);

            loginPointList = (ArrayList) ois.readObject();

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
        System.out.println("ORIGINAL");
        for (PointPair v : originalPointList) {
            System.out.println(v);
        }

        System.out.println("NEW");
        for (PointPair v : newPointList) {
            System.out.println(v);
        }

        if (originalPointList.size() == newPointList.size()) {
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

    private boolean compareVibrationLists(List<VibrationTracker> originalVibrations, List<VibrationTracker> newVibrations, long threshold) {
        System.out.println("ORIGINAL");
        for (VibrationTracker v : originalVibrations) {
            System.out.println(v);
        }

        System.out.println("NEW");
        for (VibrationTracker v : newVibrations) {
            System.out.println(v);
        }

        for (int i = 0; i < newVibrations.size(); i++) {
            if (originalVibrations.get(i).isLongPress()) {
                long lowTime = originalVibrations.get(i).getLongPressTime() - threshold;
                long highTime = originalVibrations.get(i).getLongPressTime() + threshold;

                if (!(ValueRange.of(lowTime, highTime).isValidValue(newVibrations.get(i).getLongPressTime())))
                    return false;
            } else {
                if (!(originalVibrations.get(i).getTapCount() == newVibrations.get(i).getTapCount())) {
                    return false;
                }
            }
        }

        return true;
    }

    private void createLoginVibrations() throws InterruptedException {
        Random rand = new Random();
        int vibrationType = rand.nextInt(2);
        isVibrating = true;
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        int vibrateAmount = rand.nextInt(4) + 1;
        if (vibrationType == 0) {
            isLongPress = false;
            for (int i = 0; i < vibrateAmount; i++) {
                System.out.println("Single");
                Thread.sleep(500);
                v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            vibrations.add(new VibrationTracker(false, vibrateAmount, 0));
        } else {
            isLongPress = true;
            System.out.println("Long");
            long vibrationTime = vibrateAmount * 1000;
            v.vibrate(VibrationEffect.createOneShot(vibrationTime, VibrationEffect.DEFAULT_AMPLITUDE));
            Thread.sleep(vibrationTime);
            VibrationTracker vt = new VibrationTracker(true, 0, vibrationTime);
            vibrations.add(vt);
            System.out.println("HERE");
            System.out.println(vt);
        }

        isVibrating = false;
    }

    private PointPair calculatePointPairAverage(List<PointPair> pointPairList) {
        int amount = pointPairList.size();
        int x = 0;
        int y = 0;

        for (PointPair p : pointPairList) {
            x += p.getX();
            y += p.getY();
        }

        try {
            return new PointPair(x / amount, y / amount);
        } catch (Exception e) {
            return new PointPair(-1, -1);
        }
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