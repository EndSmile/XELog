package com.ldy.xelog_read.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldy.xelog_read.R;
import com.ldy.xelog_read.widget.androidtreeview.holder.BaseNodeViewHolder;
import com.ldy.xelog_read.widget.androidtreeview.model.TreeNode;

/**
 * Created by ldy on 2017/5/24.
 */

public class TagItemHolder extends BaseNodeViewHolder<String> {

    private ImageView ivArrow;
    private CheckBox chkTag;
    private static TreeNode nodeSelect;
    private final TagChangeListener tagChangeListener;

    public TagItemHolder(Context context, TagChangeListener tagChangeListener) {
        super(context);
        this.tagChangeListener = tagChangeListener;
    }

    @Override
    public View createNodeView(TreeNode node, String value) {
        View view = LayoutInflater.from(context).inflate(R.layout.xelog_read_item_tag, null);
        chkTag = (CheckBox) view.findViewById(R.id.checkbox_item_tag);
        chkTag.setChecked(node.isSelected());

        chkTag.setOnClickListener(v -> {
            boolean isChecked = chkTag.isChecked();

            getTreeView().selectNodeAndChildren(node,isChecked,false);

            //改变其父节点
            TreeNode parentNode = node.getParent();
            while (parentNode.getParent()!=null){
                boolean parentShouldCheck = true;
                for (TreeNode childNode:parentNode.getChildren()){
                    if (!childNode.isSelected()){
                        parentShouldCheck = false;
                        break;
                    }
                }
                getTreeView().selectNode(parentNode, parentShouldCheck);
                parentNode = parentNode.getParent();
            }
            tagChangeListener.onTagChange();
        });

        TextView tvTagValue = (TextView) view.findViewById(R.id.tv_item_tag_value);
        tvTagValue.setText(value);

        ivArrow = (ImageView) view.findViewById(R.id.iv_item_tag_arrow);
        if (node.isLeaf()){
            ivArrow.setVisibility(View.GONE);
        }else {
            ivArrow.setVisibility(View.VISIBLE);
        }
        view.setPadding(60*node.getLevel(),0,0,0);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        if (ivArrow.getVisibility()==View.VISIBLE){
            if (active){
                ivArrow.setImageResource(R.drawable.xelog_read_ic_arrow_down);
            }else {
                ivArrow.setImageResource(R.drawable.xelog_read_ic_arrow_right);
            }
        }else {
            chkTag.setChecked(!chkTag.isChecked());
        }
    }
    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        chkTag.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        chkTag.setChecked(mNode.isSelected());
    }

    public interface TagChangeListener {
        /**
         * tag被改变了
         */
        void onTagChange();
    }
}
