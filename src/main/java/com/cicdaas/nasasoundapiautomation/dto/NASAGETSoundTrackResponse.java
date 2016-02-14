package com.cicdaas.nasasoundapiautomation.dto;

import java.util.List;

public class NASAGETSoundTrackResponse {

    public int count;
    public List<SoundTrack> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<SoundTrack> getResults() {
        return results;
    }

    public void setResults(List<SoundTrack> results) {
        this.results = results;
    }

}
