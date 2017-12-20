package org.tensorflow.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import static package org.tensorflow.demo.R.layout.activity_home2;

public class MushroomDetailActivity extends AppCompatActivity {

    // ImageButton cameraOpenButton;


    ImageView mImage;
    TextView mMushroomName;
    TextView mConfidence;
    TextView mEdible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("MushroomDetail","Started activity");

        setContentView(R.layout.activity_mushroom_detail);
        Intent i = getIntent();

        String name = i.getStringExtra("mushroom_name");
        String confidence = i.getStringExtra("confidence");
        String imgFilename = i.getStringExtra("image_filename");
        //String imgFilename = ClassifierActivity.IMAGE_NAME_1;

        mImage = (ImageView) findViewById(R.id.mushroom_image);
        mMushroomName = (TextView) findViewById(R.id.mushroom_name);
        mConfidence = (TextView) findViewById(R.id.classification_confidence);
        mEdible = (TextView) findViewById(R.id.edible_boolean);
        Log.i("MushroomDetail",name);
        Log.i("MushroomDetail",confidence);

        Bitmap img = loadImageFromStorage(imgFilename);
        Log.i("MushroomDetail","After load image");

        mImage.setImageBitmap(img);
        mMushroomName.setText("Mushroom: " + name);
        mConfidence.setText("Confidence :" + confidence + "%");

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        this.startActivity(i);
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


}