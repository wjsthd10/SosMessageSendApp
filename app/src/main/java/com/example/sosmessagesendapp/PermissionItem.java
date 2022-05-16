package com.example.sosmessagesendapp;

public class PermissionItem {
    int pIcon;
    String pTitle;
    String pMessage;

    public PermissionItem(int pIcon, String pTitle, String pMessage) {
        this.pIcon = pIcon;
        this.pTitle = pTitle;
        this.pMessage = pMessage;
    }

    public int getpIcon() {
        return pIcon;
    }

    public void setpIcon(int pIcon) {
        this.pIcon = pIcon;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpMessage() {
        return pMessage;
    }

    public void setpMessage(String pMessage) {
        this.pMessage = pMessage;
    }
}
