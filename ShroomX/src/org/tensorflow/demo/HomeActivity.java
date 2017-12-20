package org.tensorflow.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class HomeActivity extends AppCompatActivity {

    ImageButton cameraOpenButton;
    ImageView pastMushroom1;
    TextView pastText1;
    ImageView pastMushroom2;
    TextView pastText2;
    ImageView pastMushroom3;
    TextView pastText3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cameraOpenButton = (ImageButton) findViewById(R.id.imageButton2);
        cameraOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, ClassifierActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                HomeActivity.this.startActivity(myIntent);
            }
        });

        pastMushroom1 = (ImageView) findViewById(R.id.past_mushroom_1);
        Bitmap img1 = loadImageFromStorage(ClassifierActivity.IMAGE_NAME_1);
        pastMushroom1.setImageBitmap(Bitmap.createScaledBitmap(img1,360,360,false));
        pastText1 = (TextView) findViewById(R.id.past_mushroom_1_text);

        String name1 = readFromFile(ClassifierActivity.MUSHROOM_NAME_1);
        String conf1 = readFromFile(ClassifierActivity.MUSHROOM_CONFIDENCE_1);
        String past1 = name1 + "\n" + conf1;
        pastText1.setText(past1);


        pastMushroom2 = (ImageView) findViewById(R.id.past_mushroom_2);
        Bitmap img2 = loadImageFromStorage(ClassifierActivity.IMAGE_NAME_2);
        pastMushroom2.setImageBitmap(Bitmap.createScaledBitmap(img2,360,360,false));
        pastText2 = (TextView) findViewById(R.id.past_mushroom_2_text);

        String name2 = readFromFile(ClassifierActivity.MUSHROOM_NAME_2);
        String conf2 = readFromFile(ClassifierActivity.MUSHROOM_CONFIDENCE_2);
        String past2 = name2 + "\n" + conf2;
        pastText2.setText(past2);


        pastMushroom3 = (ImageView) findViewById(R.id.past_mushroom_3);
        Bitmap img3 = loadImageFromStorage(ClassifierActivity.IMAGE_NAME_3);
        pastMushroom3.setImageBitmap(Bitmap.createScaledBitmap(img2,360,360,false));
        pastText3 = (TextView) findViewById(R.id.past_mushroom_3_text);

        String name3 = readFromFile(ClassifierActivity.MUSHROOM_NAME_3);
        String conf3 = readFromFile(ClassifierActivity.MUSHROOM_CONFIDENCE_3);
        String past3 = name3 + "\n" + conf3;
        pastText3.setText(past3);

    }



    private Bitmap loadImageFromStorage(String filename)
    {

        try {
            File directory = getApplicationContext().getFilesDir();
            Log.i("Bitmap ", directory+filename);
            File f=new File(directory, filename);

            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void writeToFile(String data, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String filename) {

        String ret = "";

        try {
            //File directory = getApplicationContext().getFilesDir();
            //File f=new File(directory, filename);
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
/*

*/

}