package com.giovannirizzotti.mysmartbuildings.Utils;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewAnimation {

    public void animate(RecyclerView.ViewHolder holder, boolean godown) {
        slidingAmimation(holder, godown);
    }

    private void slidingAmimation(RecyclerView.ViewHolder holder, boolean godown) {
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", godown ? 200 : -200, 0);
        animatorTranslateY.setDuration(500);
        animatorTranslateY.start();
    }
}
