package ch.usi.inf.mc.shroomx;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 *
 */

public class TermsConditionsFragment extends Fragment {
    private static final String TAG = "TermsConditionsActivity";

    private TextView mShakeToAgree;

    //private Animation anim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.activity_terms, container, false);

        mShakeToAgree = (TextView) v.findViewById(R.id.shake_to_agree);

        final Animation anim = AnimationUtils.loadAnimation(container.getContext(),R.anim.rotate);
        anim.setStartOffset(2000);
        anim.setRepeatCount(Animation.INFINITE);

        return v;
    }

}
