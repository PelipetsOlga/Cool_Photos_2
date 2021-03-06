package com.mobapply.coolphotos.menu;

public class MenuItem {
    private int id;
    private int iconId;
    private int textId;
    private boolean selected;

    public MenuItem(int iconId, int textId, boolean selected){
        this.iconId = iconId;
        this.textId = textId;
        this.selected = selected;
    }

    public MenuItem(int id, int iconId, int textId, boolean selected){
        this.id = id;
        this.iconId = iconId;
        this.textId = textId;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public int getTextId(){
       return textId;
   }

    public int getIconId(){
        return iconId;
    }

    public boolean isSelected(){
        return selected;
    }

    public void setSelected(boolean value){
        this.selected=value;
    }


}
