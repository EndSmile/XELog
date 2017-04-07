package com.ldy.xelog_read.widget.tagtree;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ldy.xelog_read.R;
import com.ldy.xelog_read.control.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ldy on 2017/3/23.
 */

public class TagTree extends ViewGroup {
    public static final int TIRE_DISTANCE_DP = 50;
    public static final int CHILD_DISTANCE_DP = 10;

    private Map<View, Tag> viewTagMap = new HashMap<>();
    private List<List<View>> views = new ArrayList<>();
    private float scale;
    private Tag tag;
    private TagChangeListener tagChangeListener;
    private Paint paint;

    public TagTree(Context context) {
        this(context, null);
    }

    public TagTree(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scale = context.getResources().getDisplayMetrics().density;
        paint = new Paint();
        paint.setColor(0xff626262);
        paint.setStrokeWidth(2);
        setWillNotDraw(false);
    }

    public void goneLine(){
        setWillNotDraw(true);
    }

    public void setTag(Tag tag) {
        if (this.tag != null) {
            removeAllViews();
            viewTagMap.clear();
            views.clear();
        }
        this.tag = tag;
        addView(0, tag);
    }

    private void setTag(Tag tag, int tier) {
        for (Tag child : tag.getChildren()) {
            addView(tier, child);
        }
    }

    private void addView(int tier, Tag child) {
        CheckBox checkBox = generateCheckBox(child);
        List<View> tireViews = getOrAdd(tier);
        tireViews.add(checkBox);
        viewTagMap.put(checkBox, child);
        addView(checkBox);
        setTag(child, tier + 1);
    }

    private List<View> getOrAdd(int tire) {
        if (views.size() <= tire) {
            views.add(new ArrayList<>());
        }
        return views.get(tire);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int height = 0;
        int width = 0;
        for (List<View> tireView : views) {
            int tireHeight = 0;
            for (View tagView : tireView) {
                measureChild(tagView, widthMeasureSpec, heightMeasureSpec);
                int tagMeasuredHeight = tagView.getMeasuredHeight();
                int tagMeasuredWidth = tagView.getMeasuredWidth();
                Log.d("TagTree", "tagMeasuredWidth:" + tagMeasuredWidth);
                if (tagMeasuredHeight > tireHeight) {
                    //找到这行最高的高度
                    tireHeight = tagMeasuredHeight;
                }
                if (viewTagMap.get(tagView).isLeaf()) {
                    //宽度等于所有叶子节点的宽度之和加上间距
                    width += tagMeasuredWidth + getChildDistance();
                }
            }
            height += (int) (tireHeight + getTireDistance());
        }

        //最后会多加一个distance，去掉这个distance
        if (width >= 0) {
            width -= getChildDistance();
        }
        if (height >= 0) {
            height -= getTireDistance();
        }

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int top = 0;
        for (List<View> tireView : views) {
            //首先将节点的top布局正确
            int tireLeft = 0;
            int tireTop = top;
            for (View tagView : tireView) {
                int rc = tireLeft + tagView.getMeasuredWidth();
                int bc = tireTop + tagView.getMeasuredHeight();

                tagView.layout(tireLeft, tireTop, rc, bc);
                tireLeft += rc + CHILD_DISTANCE_DP * scale;
                if (bc > top) {
                    top = bc;
                }
            }
            top += TIRE_DISTANCE_DP;
        }

        for (int i = views.size() - 1; i >= 0; i--) {
            //再根据叶子节点的位置从最后一层倒推出上面层级的位置
            List<View> tire = this.views.get(i);
            int left = 0;
            for (View tagView : tire) {
                Tag tag = viewTagMap.get(tagView);
                if (tag.isLeaf()) {
                    //如果是叶子节点，其位置根据当前层级前一个节点的位置确定
                    int rc = left + tagView.getMeasuredWidth();
                    tagView.layout(left, tagView.getTop(), rc, tagView.getBottom());
                    left = rc;
                } else {
                    int childLeft = -1;
                    int childRight = -1;
                    for (Tag child : tag.getChildren()) {
                        View view = getViewFromTag(child);
                        if (childLeft == -1) {
                            childLeft = view.getLeft();
                        }
                        int right = view.getRight();
                        if (right > childRight) {
                            childRight = right;
                        }
                    }
                    //如果不是叶子节点，其位置相对于其子节点居中
                    int lc = childLeft + ((childRight - childLeft) / 2 - tagView.getMeasuredWidth() / 2);
                    tagView.layout(lc, tagView.getTop(), lc + tagView.getMeasuredWidth(), tagView.getBottom());
                    left = childRight;
                }
                left += getChildDistance();
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas,tag);
    }

    private void drawLine(Canvas canvas,Tag tag){
        if (!tag.isLeaf()){
            View tagView = getViewFromTag(tag);
            for (Tag child : tag.getChildren()) {
                View view = getViewFromTag(child);
                canvas.drawLine((tagView.getRight() - tagView.getLeft()) / 2 + tagView.getLeft(),
                        tagView.getBottom(),
                        (view.getRight() - view.getLeft()) / 2 + view.getLeft(),
                        view.getTop()
                        , paint     );
                drawLine(canvas,child);
            }
        }
    }

    private View getViewFromTag(Tag tag) {

        Set<View> kset = viewTagMap.keySet();
        for (View ks : kset) {
            if (tag.equals(viewTagMap.get(ks))) {
                return ks;
            }
        }
        throw new IllegalArgumentException("tag not found");
    }

    public void setTagChangeListener(TagChangeListener tagChangeListener) {
        this.tagChangeListener = tagChangeListener;
    }

    @NonNull
    private CheckBox generateCheckBox(Tag tag) {
//        CheckBox checkBox = (CheckBox) LayoutInflater.from(getContext()).inflate(R.layout.view_tag_checkbox, null);
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setText(tag.getText());
        checkBox.setChecked(tag.isChecked());

        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用点击事件处理，避免自动修改的CheckBox触发onCheckChange事件
                tag.check(checkBox.isChecked());
                updateCheckState();
                if (tagChangeListener != null) {
                    tagChangeListener.onTagChange();
                }
            }
        });

        return checkBox;
    }

    private void updateCheckState() {
        for (View view : viewTagMap.keySet()) {
            ((CheckBox) view).setChecked(viewTagMap.get(view).isChecked());
        }
    }

    private float getTireDistance() {
        return TIRE_DISTANCE_DP * scale;
    }

    private float getChildDistance() {
        return CHILD_DISTANCE_DP * scale;
    }

    public interface TagChangeListener {
        /**
         * tag被改变了
         */
        void onTagChange();
    }
}
