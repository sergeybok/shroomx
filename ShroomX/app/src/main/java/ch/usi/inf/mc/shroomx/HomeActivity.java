package shroomx.shroomx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static shroomx.shroomx.R.layout.activity_home2;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_home2);
    }
/*
public void MushroomDetailActivity(View view){
    Intent mushIntend = new Intent(HomeActivity.this,MushroomDetailActivity);
    startActivity(mushIntend);
    finish();
}
    public void MushroomDetailActivity(View view){
        Intent mushIntend = new Intent(HomeActivity.this,MushroomDetailActivity);
        startActivity(mushIntend);
        finish();
    }
    public void CameraActivity(View v) {
        Intent camIntent = new Intent(HomeActivity.this, CameraActivity.class);
        startActivity(camIntent);
        finish();
    }
*/
}