package io.smalldata.beehiveapp.RSRPBackendSupport;

import org.apache.commons.lang3.StringUtils;
import org.researchsuite.rsrp.CSVBackend.CSVEncodable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by christinatsangouri on 3/7/18.
 */

public class PAMCSVEncodable extends PAMRaw implements CSVEncodable {

    public static String TYPE = "PAMCSVEncodable";


    public PAMCSVEncodable(UUID uuid, String taskIdentifier, UUID taskRunUUID, Map<String, Serializable> choice) {
        super(uuid, taskIdentifier, taskRunUUID, choice);
    }

    @Override
    public String[] toRecords() {
        Collection resultMapValuesCollection = this.getPamChoice().values();
        String[] resultMapValues = Arrays.copyOf(resultMapValuesCollection.toArray(), resultMapValuesCollection.toArray().length, String[].class);

        StringBuilder recordBuilder = new StringBuilder();

        for (String result:resultMapValues){
            recordBuilder.append(result);
            recordBuilder.append(",");
        }


        String record = recordBuilder.toString();
        String completeRecord = getTimestamp() + "," + record;
        String[] completeRecordArray = new String[]{completeRecord};

        return completeRecordArray;
    }

    @Override
    public String getTypeString() {
        return this.getTaskIdentifier();
    }

    @Override
    public String getHeader() {
        String[] pamHeader = new String[]{"timestamp","affect_arousal","affect_valence","positive_affect","mood","negative_affect"};
        String pamHeaderJoined = StringUtils.join(pamHeader,",");
        return pamHeaderJoined;
    }

    private String getTimestamp () {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int zone = calendar.get(Calendar.ZONE_OFFSET);

        StringBuilder timestampBuilder = new StringBuilder();
        timestampBuilder.append(year);
        timestampBuilder.append("-");
        timestampBuilder.append(month);
        timestampBuilder.append("-");
        timestampBuilder.append(date);
        timestampBuilder.append("T");
        timestampBuilder.append(hour);
        timestampBuilder.append(":");
        timestampBuilder.append(minute);
        timestampBuilder.append(":");
        timestampBuilder.append(second);
        timestampBuilder.append("-");
        timestampBuilder.append(zone);

        String timestamp = timestampBuilder.toString();

        return timestamp;
    }
}
