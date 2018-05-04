package org.researchsuite.rsrp.CSVBackend;

public interface CSVEncodable extends CSVConvertible {

    String[] toRecords();
}
