package com.avatlantik.cooperative.service;

import com.avatlantik.cooperative.model.json.DownloadResponse;
import com.avatlantik.cooperative.model.json.UploadRequest;
import com.avatlantik.cooperative.model.json.UploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SyncService {

    @GET("cooperative/hs/exchange/dataDTO")
    Call<DownloadResponse> download();

    @Multipart
    @POST("cooperative/hs/exchange/dataDTO")
    Call<UploadResponse> uploadWithDocuments(@Part("track") UploadRequest request,
                                             @Part List<MultipartBody.Part> documents);
}
