package com.ldy.xelog_read.control;

import com.ldy.xelog_read.widget.androidtreeview.model.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ldy on 2017/5/24.
 */

public class TagTreeNode extends TreeNode {
    public TagTreeNode(Object value) {
        super(value);
    }

    public static TagTreeNode root() {
        TagTreeNode root = new TagTreeNode(null);
        root.setSelectable(false);
        return root;
    }

    public TagTreeNode addChildByValuePath(List<?> path, boolean isSelect) {
        TagTreeNode localNode = this;
        for (Object o : path) {
            localNode = localNode.addChild(o);
        }
        localNode.setSelected(isSelect);
        return this;
    }

    /**
     * 添加一个{@link TagTreeNode}
     *
     * @param value 待添加
     * @return 如果添加成功, 返回参数node, 如果元素已存在, 返回原树中的node元素
     */
    public TagTreeNode addChild(Object value) {
        TagTreeNode node = containsNode(value);
        if (node == null) {
            node = new TagTreeNode(value);
            addChild(node);
        }
        return node;

    }

    private TagTreeNode containsNode(Object value) {
        for (TagTreeNode treeNode : getTagChildren()) {
            if (treeNode.getValue().equals(value)) {
                return treeNode;
            }
        }
        return null;
    }


    /**
     * 根据叶子节点的状态修复当前节点的所有非叶子子节点状态
     */
    public void trim() {
        setSelected(trimSelf());
    }

    private boolean trimSelf() {
        if (isLeaf()) {//叶子节点
            return isSelected();
        }

        return trimChildren();
    }

    private boolean trimChildren() {
        boolean result = true;
        for (TagTreeNode treeNode : getTagChildren()) {
            treeNode.trim();
            if (!treeNode.isSelected()) {
                result = false;
            }
        }
        return result;
    }

    /**
     * @return path不包含最顶层的rootNode
     */
    public List<List<?>> getAllSelectedPath() {
        List<TagTreeNode> selectedLeafList = getSelectedLeafList(new ArrayList<>());
        List<List<?>> paths = new ArrayList<>();
        for (TagTreeNode leaf : selectedLeafList) {
            LinkedList<Object> path = new LinkedList<>();
            TagTreeNode node = leaf;
            while (node.getParent() != null) {
                path.addFirst(node.getValue());
                node = node.getParent();
            }
            paths.add(path);
        }
        return paths;
    }

    /**
     * 获取全部的叶子节点
     *
     * @param leafList 初始的list
     * @return 放入结果的list
     */
    private List<TagTreeNode> getSelectedLeafList(List<TagTreeNode> leafList) {
        if (isLeaf()) {
            if (isSelected()) {
                leafList.add(this);
            }
        } else {
            for (TagTreeNode child : getTagChildren()) {
                child.getSelectedLeafList(leafList);
            }
        }
        return leafList;
    }

    /**
     * 获取全部子节点，不包括root节点
     */
    public List<TagTreeNode> getAllNode(List<TagTreeNode> nodeList) {
        if (getParent() != null) {
            //不添加root节点
            nodeList.add(this);
        }
        if (!isLeaf()) {
            for (TagTreeNode child : getTagChildren()) {
                child.getAllNode(nodeList);
            }
        }
        return nodeList;
    }

    private List<TagTreeNode> getTagChildren(){
        List temp = children;
        return temp;
    }

    @Override
    public TagTreeNode getParent() {
        return (TagTreeNode) super.getParent();
    }
}
