package com.zr.deliver.frame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.zr.deliver.R;

import static android.support.v4.view.ViewCompat.animate;

/**
 * Created by Administrator on 2015/6/17.
 */
public class CustomToast {

    private Context context;

    public CustomToast(Context context) {

        this.context = context;

        mOverShootInter = new OvershootInterpolator();
    }

    private Interpolator mOverShootInter;


    public float dp2px(Context cxt, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                cxt.getResources().getDisplayMetrics());
    }


    public void showCustomToast(CharSequence toastMessage, int duration) {
        final WindowManager windowManager;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final RelativeLayout addedLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.custom_toast, null);
        final TextView textView = (TextView) addedLayout.findViewById(R.id.toast_text);
        textView.setText(toastMessage);
        final ValueAnimator anim = ValueAnimator.ofInt(0, 1);
        anim.setDuration(duration);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    animate(textView).translationY(ViewHelper.getTranslationY(textView) + dp2px(context, 50)).setDuration(
                            200).setInterpolator(mOverShootInter);
                    windowManager.removeView(addedLayout);
                } catch (Exception e) {
                }
            }
        });
        anim.start();
        WindowManager.LayoutParams addedLayoutParams;
        addedLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN, PixelFormat.TRANSPARENT);
        addedLayoutParams.x = 0;
        addedLayoutParams.y = 0;
        windowManager.addView(addedLayout, addedLayoutParams);
        animate(textView).translationY(ViewHelper.getTranslationY(textView) - dp2px(context, 50)).setDuration(
                200).setInterpolator(mOverShootInter);


    }

    private static class OvershootInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            if (input < 0.5f)
                return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
            else
                return 0.5f * (o(input * 2.0f - 2.0f, 4) + 2.0f);
        }

        private float o(float t, float s) {
            return t * t * ((s + 1) * t + s);
        }
    }
}
