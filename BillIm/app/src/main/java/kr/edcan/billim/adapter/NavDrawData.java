package kr.edcan.billim.adapter;

import android.content.Context;

/**
 * Created by kotohana5706 on 15. 7. 13.
 */
public class NavDrawData {
    private String List;
    private int icon;

    public NavDrawData(Context context, String List_, int icon_) {
        List = List_;
        icon = icon_;
    }

    public String getList() {
        return List;
    }

    public int getIcon() {
        return icon;
    }
}