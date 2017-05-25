package com.avatlantik.cooperative.model.json;

import com.google.gson.annotations.Expose;

public class DownloadResponse {
    @Expose
    private DownloadTrackDTO track;

    public DownloadResponse(DownloadTrackDTO track) {
        this.track = track;
    }

    public DownloadTrackDTO getTrack() {
        return track;
    }
}
