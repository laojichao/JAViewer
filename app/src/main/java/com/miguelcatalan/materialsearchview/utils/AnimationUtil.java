package com.miguelcatalan.materialsearchview.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;

public class AnimationUtil {

    public static final int ANIMATION_DURATION_MEDIUM = 400;

    public interface AnimationListener {
        boolean onAnimationStart(View view);
        boolean onAnimationEnd(View view);
        boolean onAnimationCancel(View view);
    }

    public static void reveal(final View view, final AnimationListener listener) {
        int cx = view.getWidth();
        int cy = view.getHeight() / 2;
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        view.setVisibility(View.VISIBLE);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null) listener.onAnimationStart(view);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null) listener.onAnimationEnd(view);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (listener != null) listener.onAnimationCancel(view);
            }
        });

        anim.setDuration(ANIMATION_DURATION_MEDIUM);
        anim.start();
    }

    public static void fadeInView(final View view, int duration, final AnimationListener listener) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (listener != null) listener.onAnimationStart(view);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) listener.onAnimationEnd(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (listener != null) listener.onAnimationCancel(view);
                }
            })
            .start();
    }

    public static void fadeOutView(final View view, int duration, final AnimationListener listener) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (listener != null) listener.onAnimationStart(view);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    view.setAlpha(1f);
                    if (listener != null) listener.onAnimationEnd(view);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (listener != null) listener.onAnimationCancel(view);
                }
            })
            .start();
    }
}
