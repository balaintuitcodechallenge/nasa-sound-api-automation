package com.cicdaas.nasasoundapiautomation.dto;

import org.springframework.http.HttpHeaders;

public class NASAGETSoundTrackResponseHolder {

    protected HttpHeaders headers;
    protected NASAGETSoundTrackResponse response;

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public NASAGETSoundTrackResponse getResponse() {
        return response;
    }

    public void setResponse(NASAGETSoundTrackResponse response) {
        this.response = response;
    }

}
