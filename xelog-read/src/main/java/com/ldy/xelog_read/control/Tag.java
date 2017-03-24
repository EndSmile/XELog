package com.ldy.xelog_read.control;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by ldy on 2017/3/6.
 */
public class Tag {
    private final String text;
    private Set<Tag> children = new LinkedHashSet<>();
    private Tag parent;
    private boolean isChecked = true;

    public Tag(String text) {
        this.text = text;
    }

    /**
     * 添加一个{@link Tag}
     *
     * @param s 待添加
     * @return 如果添加成功, 返回参数tag, 如果元素已存在, 返回原树中的tag元素
     */
    public Tag addTag(String s) {
        Tag tag = containsTag(s);
        if (tag == null) {
            tag = new Tag(s);
            children.add(tag);
            tag.setParent(this);
        }
        return tag;

    }

    private Tag containsTag(String s) {
        for (Tag tag : children) {
            if (tag.text.equals(s)) {
                return tag;
            }
        }
        return null;
    }

    public void addTag(List<String> tag, boolean isCheck) {
        Tag localTag = this;
        for (String s : tag) {
            localTag = localTag.addTag(s);
        }
        localTag.isChecked = isCheck;
    }

    /**
     * 根据叶子节点的状态修复当前节点的所有非叶子子节点状态
     */
    public void trim() {
        isChecked = trimSelf();
    }

    private boolean trimSelf() {
        if (children.isEmpty()) {//叶子节点
            return isChecked;
        }

        return trimChildren();
    }

    private boolean trimChildren() {
        boolean result = true;
        for (Tag tag : children) {
            tag.trim();
            if (!tag.isChecked) {
                result = false;
            }
        }
        return result;
    }


    public Set<Tag> getChildren() {
        return children;
    }

    public String getText() {
        return text;
    }

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    public Tag getParent() {
        return parent;
    }

    void setParent(Tag parent) {
        this.parent = parent;
    }

    public void check(boolean check) {
        checkChildren(check);
        Tag root = getRoot();
        root.trim();
    }

    public List<List<String>> getAllPath() {
        List<Tag> leafList = getLeafList(new ArrayList<>());
        List<List<String>> paths = new ArrayList<>();
        for (Tag leaf : leafList) {
            LinkedList<String> path = new LinkedList<>();
            paths.add(leaf.getPathFromTag(path));
        }
        return paths;
    }

    private LinkedList<String> getPathFromTag(LinkedList<String> path) {
        path.addFirst(getText());
        if (getParent() != null) {
            getParent().getPathFromTag(path);
        }
        return path;
    }

    /**
     * 获取全部的叶子节点
     * @param leafList 初始的list
     * @return  放入结果的list
     */
    private List<Tag> getLeafList(List<Tag> leafList) {
        if (isLeaf()) {
            if (isChecked()){
                leafList.add(this);
            }
        } else {
            for (Tag child : getChildren()) {
                child.getLeafList(leafList);
            }
        }
        return leafList;
    }

    private Tag getRoot() {
        if (parent != null) {
            return parent.getRoot();
        } else {
            return this;
        }
    }

    private void checkChildren(boolean check) {
        isChecked = check;
        for (Tag child : children) {
            child.checkChildren(check);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

}
