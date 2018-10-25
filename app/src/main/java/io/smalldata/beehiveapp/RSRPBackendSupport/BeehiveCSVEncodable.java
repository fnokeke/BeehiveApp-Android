package io.smalldata.beehiveapp.RSRPBackendSupport;

import org.apache.commons.lang3.StringUtils;
import org.researchsuite.rsrp.CSVBackend.CSVEncodable;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by christinatsangouri on 4/27/18.
 */

public class BeehiveCSVEncodable extends BeehiveResult implements CSVEncodable {

    public static String TYPE = "BeehiveCSVEncodable";

    public BeehiveCSVEncodable(UUID uuid, String taskIdentifier, UUID taskRunUUID, Object demographyResults[], String headerValues[]) {
        super(uuid, taskIdentifier, taskRunUUID, demographyResults, headerValues);
    }

    @Override
    public String[] toRecords() {

        Object[] results = this.getDemographyResults();
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(getTimestamp());
        resultBuilder.append(",");

        for (Object result : results) {
            resultBuilder.append(String.valueOf(result));
            resultBuilder.append(",");
        }

        String record = resultBuilder.toString();
        String[] completeRecordArray = new String[]{record};

        return completeRecordArray;
    }

    @Override
    public String getTypeString() {
        return this.getTaskIdentifier();
    }

    @Override
    public String getHeader() {

        String[] header = this.getHeaderValues();

        String headerJoined = StringUtils.join(header, ",");
        return headerJoined;
    }

    private String getTimestamp() {
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