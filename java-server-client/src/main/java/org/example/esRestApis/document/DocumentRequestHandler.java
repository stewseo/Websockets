package org.example.esRestApis.document;

import org.example.esRestApis.AbstractApiHandler;

import java.util.Objects;

public class DocumentRequestHandler extends AbstractApiHandler {
    protected String index = "/kibana_sample_data_ecommerce";
    private String documentModule = "/_doc";
    protected String numberOfDocuments;
    public DocumentRequestHandler() {}

    public String buildCurl(String method, String endpoint) throws Exception {
        if(method == null || endpoint == null) {
            throw new Exception("Throw exception");
        }
        return sendRequest("-X GET", endpoint + documentModule);

    }

    @Override
    public String toString(){
        return "Current index: " + index + " total documents: " + numberOfDocuments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentRequestHandler that)) {
            return false;
        }
        return Objects.equals(index, that.index) && Objects.equals(numberOfDocuments, that.numberOfDocuments);
    }

}

