package org.researchsuite.rsrp.CSVBackend;

/**
 * Created by Christina on 1/31/2018.
 */

public class TempQueueElement {

    public String typeString;
    public String header;
    public String[] records;

    public TempQueueElement(String typeString, String header, String[] records){
        this.typeString = typeString;
        this.header = header;
        this.records = records;
    }


}
