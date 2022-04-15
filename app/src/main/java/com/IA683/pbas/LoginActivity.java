package com.IA683.pbas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private List<PointPair> pointList = new ArrayList<>();

    ImageView passwordImage;
    Bitmap bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordImage = (ImageView) findViewById(R.id.login_password_image);
        deserializePointData();
        bmp = loadImageBitmap(getApplicationContext(), "img");
        passwordImage.setImageBitmap(bmp);
    }

    private static final int PICK_IMAGE_FILE = 2;

    private void deserializePointData() {
        try
        {
            FileInputStream fis = new FileInputStream(getApplicationInfo().dataDir + "/point_data");
            ObjectInputStream ois = new ObjectInputStream(fis);

            pointList = (ArrayList) ois.readObject();

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

    public Bitmap loadImageBitmap(Context context, String name){
        name = name + ".jpg";
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = context.openFileInput(name);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}