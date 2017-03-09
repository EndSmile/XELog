package com.ldy.xelog_read.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ldy on 2017/3/6.
 */
public class Tag {
    private final String text;
    private Set<Tag> children;
    private Tag parent;
    private boolean isCheck = true;

    public Tag(String text) {
        this.text = text;
    }

    /**
     * 添加一个{@link Tag}
     * @param tag 待添加
     * @return  如果添加成功,返回参数tag,如果元素已存在导致失败,返回原树中的tag元素
     */
    public Tag addTag(Tag tag) {
        if (children == null) {
            children = new HashSet<>();
        }

        boolean add = children.add(tag);
        if (add) {
            tag.setParent(this);
            return tag;
        } else {
            while (iterator().hasNext()) {
                Tag next = iterator().next();
                if (next.equals(tag)) {
                    return next;
                }
            }
        }
        throw new RuntimeException("impossible error");
    }


    public void addTag(List<String> tag) {
        Tag localTag = this;
        for (String s : tag) {
            localTag = localTag.addTag(new Tag(s));
        }
    }

    public Iterator<Tag> iterator() {
        return children.iterator();
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

    public void check() {
        isCheck = true;
    }

    public void disCheck() {
        isCheck = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return text.equals(tag.text);

    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
