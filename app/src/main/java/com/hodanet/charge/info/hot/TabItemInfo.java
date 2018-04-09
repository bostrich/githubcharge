package com.hodanet.charge.info.hot;

/**
 *
 */

public class TabItemInfo implements Comparable<TabItemInfo> {

    private int id;
    private Integer position;
    private String innerName;
    private String text;
    private String clickAction;

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(TabItemInfo o) {
        return this.position.compareTo(o.getPosition());
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getInnerName() {
        return innerName;
    }

    public void setInnerName(String innerName) {
        this.innerName = innerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
