package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by saurabh on 2/2/16.
 */
public class SunsetFragment extends Fragment {

    private View sceneView;
    private View sunView;
    private View skyView;

    private int blueSkyColor;
    private int sunsetSkyColor;
    private int nightSkyColor;

    private float topSunPostition;
    private boolean sunRisen = true;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        sceneView = view;
        sunView = view.findViewById(R.id.sun);
        skyView = view.findViewById(R.id.sky);

        Resources resources = getResources();

        if (Build.VERSION.SDK_INT < 23) {
            blueSkyColor = resources.getColor(R.color.blue_sky);
            sunsetSkyColor = resources.getColor(R.color.sunset_sky);
            nightSkyColor = resources.getColor(R.color.night_sky);
        } else {
            blueSkyColor = resources.getColor(R.color.blue_sky, getActivity().getTheme());
            sunsetSkyColor = resources.getColor(R.color.sunset_sky, getActivity().getTheme());
            nightSkyColor = resources.getColor(R.color.night_sky, getActivity().getTheme());
        }

        sunView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                topSunPostition = sunView.getTop();
            }
        });

        sceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sunRisen)
                    sunset();
                else
                    sunrise();
            }
        });

        return view;
    }

    private void sunset() {
        float sunYStart = sunView.getTop();
        float sunYEnd = skyView.getHeight();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(sunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();

        sunRisen = false;
    }

    private void sunrise() {
        float sunYStart = skyView.getHeight();
        float sunYEnd = topSunPostition;

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(sunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(skyView, "backgroundColor", nightSkyColor, sunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(skyView, "backgroundColor", sunsetSkyColor, sunsetSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();

        sunRisen = true;
    }
}
