package kr.edcan.billim.adapter;

import android.content.Context;

/**
 * Created by kotohana5706 on 15. 7. 13.
 */
public class CData {
    private int icon;
    private String confirm;
    private String content_label;
    private String description;
    private int id;

    public CData(Context context, int id_, int icon_, String content_label_, String description_, String confirm_) {
        id = id_;
        icon = icon_;
        confirm = confirm_;
        content_label = content_label_;
        description = description_;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }

    public String getConfirm() {
        return confirm;
    }

    public String getContent_label() {
        return content_label;
    }

    public String getDescription() {
        return description;
    }
}
