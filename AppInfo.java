package com.axel.datatracking;

import android.graphics.drawable.Drawable;

class AppInfo {

    private String AppName;
    private Drawable AppLogo;
    private boolean flag;
    private String AppFullName;

    AppInfo (String name, Drawable logo, String title) {
        this.AppName = name;
        this.AppLogo = logo;
        this.AppFullName = title;
    }

    Drawable getAppLogo() {
        return AppLogo;
    }

    String getAppName() {
        return AppName;
    }

    boolean getFlag() {
        return flag;
    }

    void setFlag(boolean flag) {
        this.flag = flag;
    }

    String getAppFullName() {
        return AppFullName;
    }
}
