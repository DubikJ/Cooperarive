package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public class UploadRequest {
    @Expose
    private TrackDTO track;

    public UploadRequest(TrackDTO track) {
        this.track = track;
    }

    public TrackDTO getTrack() {
        return track;
    }
}
