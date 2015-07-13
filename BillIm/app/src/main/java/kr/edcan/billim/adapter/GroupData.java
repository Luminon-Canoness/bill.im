package kr.edcan.billim.adapter;

import android.content.Context;

/**
 * Created by Junseok on 2015-04-16.
 */
public class GroupData {

    String groupName;
    public GroupData(Context context, String groupName_) {
        groupName = groupName_;
    }

    public String getGroupName() {return groupName;}
}
