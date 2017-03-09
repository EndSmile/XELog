package com.ldy.xelog_read.widget.dropGroup;

import android.animation.Animator;
import android.content.Context;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.ldy.xelog_read.R;


/**
 * Created by ldy on 2016/11/29.
 */

public class DropGroup extends FrameLayout {

    private OnFoldChangeListener onFoldChangeListener;

    public DropGroup(Context context) {
        super(context);
    }

    public DropGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.color.xelog_read_scrim);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fold(true);
            }
        });
//        setVisibility(GONE);
        post(new Runnable() {
            @Override
            public void run() {
                fold(false);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i=0;i<getChildCount();i++){
            getChildAt(i).setClickable(true);
        }
    }

    public void setOnFoldChangeListener(OnFoldChangeListener onFoldChangeListener) {
        this.onFoldChangeListener = onFoldChangeListener;
    }

    public void setCheckBox(final CheckBox checkBox){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spread(true);
                } else {
                    fold(true);
                }
            }
        });
        setOnFoldChangeListener(new DropGroup.OnFoldChangeListener() {
            @Override
            public void onFoldChange(boolean isFold) {
                checkBox.setChecked(!isFold);
            }
        });
    }

    public void fold(boolean isAnimate){
        if (isAnimate){
            animate().translationY(-getHeight())
                    .setDuration(200)
                    .setInterpolator(new LinearOutSlowInInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (onFoldChangeListener!=null){
                                onFoldChangeListener.onFoldChange(true);
                            }
                            setVisibility(GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
        }else {
            setTranslationY(-getHeight());
            setVisibility(GONE);
            if (onFoldChangeListener!=null){
                onFoldChangeListener.onFoldChange(true);
            }
        }
    }

    public void spread(boolean isAnimate){
        setVisibility(VISIBLE);
        if (isAnimate){
            animate().translationY(0)
                    .setDuration(200)
                    .setInterpolator(new LinearOutSlowInInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (onFoldChangeListener!=null){
                                onFoldChangeListener.onFoldChange(false);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
        }else {
            setTranslationY(0);
            setVisibility(VISIBLE);
            if (onFoldChangeListener!=null){
                onFoldChangeListener.onFoldChange(false);
            }
        }
    }

    public interface OnFoldChangeListener{
        void onFoldChange(boolean isFold);
    }
}
