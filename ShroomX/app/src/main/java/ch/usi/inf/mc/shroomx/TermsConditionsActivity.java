package ch.usi.inf.mc.shroomx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 *
 */

public class TermsConditionsActivity extends AppCompatActivity {
    private static final String TAG = "TermsConditionsActivity";

    private TextView mShakeToAgree;

    //private Animation anim;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_terms);
        mShakeToAgree = (TextView) findViewById(R.id.shake_to_agree);

        final Animation anim = AnimationUtils.loadAnimation(this,R.anim.rotate);
        anim.setStartOffset(2000);
        anim.setRepeatCount(Animation.INFINITE);


    }

}
