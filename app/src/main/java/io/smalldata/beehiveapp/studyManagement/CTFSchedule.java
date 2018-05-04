package io.smalldata.beehiveapp.studyManagement;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by jameskizer on 1/19/17.
 */
public class CTFSchedule {

    private String type;
    private String identifier;
    private String title;
    private String guid;

    private List<CTFScheduleItem> items;

    public List<CTFScheduleItem> getScheduleItems() {
        return items;
    }

    @Nullable
    public CTFScheduleItem getScheduleItem(String guid) {
        for (CTFScheduleItem item : this.items) {
            if (item.guid.equals(guid)) {
                return item;
            }
        }
        return null;
    }
}
