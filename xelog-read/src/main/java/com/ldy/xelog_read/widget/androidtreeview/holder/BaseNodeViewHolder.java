package com.ldy.xelog_read.widget.androidtreeview.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ldy.xelog_read.R;
import com.ldy.xelog_read.widget.androidtreeview.model.TreeNode;
import com.ldy.xelog_read.widget.androidtreeview.view.AndroidTreeView;
import com.ldy.xelog_read.widget.androidtreeview.view.TreeNodeWrapperView;

/**
 * Created by ldy on 2017/5/24.
 */

public abstract class BaseNodeViewHolder<E> {
    protected AndroidTreeView tView;
    protected TreeNode mNode;
    private View mView;
    protected int containerStyle;
    protected Context context;

    public BaseNodeViewHolder(Context context) {
        this.context = context;
    }

    public View getView() {
        if (mView != null) {
            return mView;
        }
        final View nodeView = getNodeView();
        final TreeNodeWrapperView nodeWrapperView = new TreeNodeWrapperView(nodeView.getContext(), getContainerStyle());
        nodeWrapperView.insertNodeView(nodeView);
        mView = nodeWrapperView;

        return mView;
    }

    public void setTreeViev(AndroidTreeView treeViev) {
        this.tView = treeViev;
    }

    public AndroidTreeView getTreeView() {
        return tView;
    }

    public void setContainerStyle(int style) {
        containerStyle = style;
    }

    public View getNodeView() {
        return createNodeView(mNode, (E) mNode.getValue());
    }

    public ViewGroup getNodeItemsView() {
        return (ViewGroup) getView().findViewById(R.id.xelog_read_node_items);
    }

    public boolean isInitialized() {
        return mView != null;
    }

    public int getContainerStyle() {
        return containerStyle;
    }


    public abstract View createNodeView(TreeNode node, E value);

    public void toggle(boolean active) {
        // empty
    }

    public void toggleSelectionMode(boolean editModeEnabled) {
        // empty
    }

    public void setmNode(TreeNode mNode) {
        this.mNode = mNode;
    }
}
