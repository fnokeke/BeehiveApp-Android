package io.smalldata.beehiveapp.onboarding;

import java.util.ArrayList;

/**
 * Created by fnokeke on 1/1/18.
 * Creates user window e.g. Noon - 3pm, 3pm - 6pm, 6pm - 9pm
 */

class GenerateUserWindow {

    /**
     * @param wakeTime24Clock  e.g. 10:05, 10:07, 10:35  become 11:00
     * @param sleepTime24Clock e.g. 10:05, 10:07, 10:35  become 10:00
     * @param hourWindow       e.g. 3 for 3 hours
     * @return StringArray e.g.  {"6am to Noon", "Noon to 6pm", "6pm to Midnight"}
     */
    static String[] generateWindowList(String wakeTime24Clock, String sleepTime24Clock, int hourWindow) {
        int startHour = roundUp(wakeTime24Clock);
        int endHour = roundDown(sleepTime24Clock);

        if (endHour < startHour) { // e.g. 10am to 1am -> 10 to 25
            endHour += 24;
        }

        ArrayList<String> entries = new ArrayList<>();
        String entry;

        for (int i = startHour; i + hourWindow <= endHour; i += hourWindow) {
            entry = String.format("%s to %s", toAmPm(i), toAmPm(i + hourWindow));
            entries.add(entry);
        }

        String[] window = {"6am to Noon", "Noon to 6pm", "6pm to Midnight"};
        if (entries.size() > 0) {
            window = entries.toArray(new String[entries.size()]);
        }
        return window;
    }

    /**
     * @param startTime: e.g. 10:05, 10:07, 10:35  become 11
     * @return int e.g. 11
     */
    private static int roundUp(String startTime) {
        String[] startTimeArr = startTime.split(":");
        int hr = Integer.parseInt(startTimeArr[0]);
        int mins = Integer.parseInt(startTimeArr[1]);
        if (mins > 0) {
            hr += 1;
        }
        return hr;
    }

    /**
     * @param endTime e.g. 10:05, 10:07, 10:35  become 10
     * @return int e.g. 10
     */
    private static int roundDown(String endTime) {
        String[] endTimeArr = endTime.split(":");
        return Integer.parseInt(endTimeArr[0]);
    }

    private static String toAmPm(int hourOfDay) {
        String result = String.format("%sam", hourOfDay);
        if (hourOfDay == 12) {
            result = "Noon";
        } else if (hourOfDay > 12 && hourOfDay < 24) {
            result = String.format("%spm", hourOfDay % 12);
        } else if (hourOfDay == 24) {
            result = "Midnight";
        } else if (hourOfDay > 24) {
            result = String.format("%sam", hourOfDay % 24);
        }
        return result;
    }

}
