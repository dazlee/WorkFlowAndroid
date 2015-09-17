package com.bananaplan.workflowandroid.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.worker.attendance.LeaveData;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;

import java.util.ArrayList;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class WorkerItem extends IdData {

    public static Drawable sDefaultAvatarDrawable;

    public String title;
    public long factoryId;
    public TaskItem currentTaskItem;
    public ArrayList<TaskItem> taskItems;
    public String address;
    public String phone;
    public int score;
    public ArrayList<BaseData> records = new ArrayList<>();
    public ArrayList<LeaveData> leaveDatas = new ArrayList<>();
    public boolean isOverTime = false;

    private Drawable avatar;

    public WorkerItem(final Context context, long id, String name, String title) {
        this(context, id, name, title, new ArrayList<TaskItem>());
    }

    public WorkerItem(final Context context, long id, String name, String title, ArrayList<TaskItem> taskItems) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.taskItems = taskItems;
        if (sDefaultAvatarDrawable == null) {
            sDefaultAvatarDrawable = context.getDrawable(R.drawable.ic_person_black);
        }
    }

    public Drawable getAvator() {
        if (this.avatar != null) {
            return this.avatar;
        }
        return sDefaultAvatarDrawable;
    }

    public boolean hasCurrentTaskItem() {
        return currentTaskItem != null;
    }
}