package com.avatlantik.cooperative.task;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.model.APIError;
import com.avatlantik.cooperative.model.db.Document;
import com.avatlantik.cooperative.model.db.DocumentStats;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.model.db.MilkParam;
import com.avatlantik.cooperative.model.db.ServiceDemand;
import com.avatlantik.cooperative.model.db.Track;
import com.avatlantik.cooperative.model.db.Visit;
import com.avatlantik.cooperative.model.json.DownloadResponse;
import com.avatlantik.cooperative.model.json.DownloadTrackDTO;
import com.avatlantik.cooperative.model.json.ExternalIdPair;
import com.avatlantik.cooperative.model.json.UploadMemberDTO;
import com.avatlantik.cooperative.model.json.UploadRequest;
import com.avatlantik.cooperative.model.json.UploadResponse;
import com.avatlantik.cooperative.model.json.UploadTrackDTO;
import com.avatlantik.cooperative.model.json.UploadedDocument;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.service.SyncService;
import com.avatlantik.cooperative.util.ErrorUtils;
import com.avatlantik.cooperative.util.NetworkUtils;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.avatlantik.cooperative.common.Consts.ROOT_DIR;
import static com.avatlantik.cooperative.common.Consts.STATUS_ERROR_SYNC;
import static com.avatlantik.cooperative.common.Consts.STATUS_FINISHED_SYNC;
import static com.avatlantik.cooperative.common.Consts.STATUS_STARTED_SYNC;
import static com.avatlantik.cooperative.util.Db2JsonModelConverter.convertMember;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertDocsStats;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertDocumentCodes;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertMemberStats;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertMembers;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertMilkStats;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertServiceCodes;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertServicesDelivery;
import static com.avatlantik.cooperative.util.Json2DbModelConverter.convertTrack;

public class SyncIntentService extends IntentService {
    public static final String SYNC_RECEIVER = "sync_receiver";
    public static final String DELIMITER = "_";

    @Inject
    DataRepository dataRepository;
    @Inject
    NetworkUtils networkUtils;
    @Inject
    ErrorUtils errorUtils;

    private SyncService syncService;

    public SyncIntentService() {
        super(SyncIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((CooperativeApplication) getApplication()).getComponent().inject(this);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(SYNC_RECEIVER);
        final Bundle bundle = new Bundle();

        if (!networkUtils.checkEthernet()) {
            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.error_internet_connecting));
            receiver.send(STATUS_ERROR_SYNC, bundle);
            return;
        }

        if (networkUtils.checkWIFIconnectionToEcomilk()) {
            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.ecomilk_connection));
            receiver.send(STATUS_ERROR_SYNC, bundle);
            return;
        }

        syncService = SyncServiceFactory.createService(
                SyncService.class,
                this.getBaseContext());

        receiver.send(STATUS_STARTED_SYNC, bundle);
        Track latestTrack = dataRepository.getLatestTrack();
        if (latestTrack != null) {
            final UploadRequest request = new UploadRequest(getUploadData(latestTrack));
            List<MultipartBody.Part> documents = getDocuments(latestTrack);
            try {
                Response<UploadResponse> uploadResponse = syncService.uploadWithDocuments(request, documents).execute();
                if (uploadResponse.isSuccessful()) {
                    UploadResponse response = uploadResponse.body();
                    if (response.getExternalIdPairs() != null) {
                        for (ExternalIdPair externalIdPair : response.getExternalIdPairs()) {
                            dataRepository.updateMemberExternalId(
                                    request.getTrack().getId(),
                                    externalIdPair.getAppExternalId(),
                                    externalIdPair.getNewExternalId());
                        }
                    }

                    if (response.getUploadedDocuments() != null) {
                        clearDocuments(response.getUploadedDocuments());
                    }

                } else {
                    APIError error = errorUtils.parseErrorCode(uploadResponse.code());
                    bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
                    receiver.send(STATUS_ERROR_SYNC, bundle);
                    return;
                }
            } catch (Exception exception) {
                APIError error = errorUtils.parseErrorMessage(exception);
                bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
                receiver.send(STATUS_ERROR_SYNC, bundle);
                return;
            }
        }
        try {
            Response<DownloadResponse> downloadResponse = syncService.download().execute();
            if (downloadResponse.isSuccessful()) {
                DownloadResponse body = downloadResponse.body();
                if(body.getTrack() == null) {
                    bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.error_no_track));
                    receiver.send(STATUS_ERROR_SYNC, bundle);
                    return;
                }
                updateDb(body);
                bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.sync_success));
                receiver.send(STATUS_FINISHED_SYNC, bundle);
            } else {
                APIError error = errorUtils.parseErrorCode(downloadResponse.code());
                bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
                receiver.send(STATUS_ERROR_SYNC, bundle);

            }
        } catch (Exception exception) {
            APIError error = errorUtils.parseErrorMessage(exception);
            bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
            receiver.send(STATUS_ERROR_SYNC, bundle);
        }

    }

    private List<MultipartBody.Part> getDocuments(Track latestTrack) {
        List<MultipartBody.Part> documentsDiff = new ArrayList<>();
        List<Member> members = dataRepository.getChangedMembers(latestTrack.getExternalId());//getMembers(latestTrack.getExternalId());

        for (Member member : members) {
            List<DocumentStats> documentStats = dataRepository.getDocStatsByMemberId(member.getExternalId());

            for (DocumentStats doc : documentStats) {
                if (!doc.isValue()) {
                    File documentFile = getDocumentFile(doc.getMemberId(), doc.getCode());
                    if (documentFile == null) continue;

                    RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), documentFile);
                    documentsDiff.add(MultipartBody.Part.createFormData("document" + DELIMITER + doc.getMemberId() + DELIMITER + doc.getCode(),
                            documentFile.getName(),
                            request));
                }
            }
        }

        return documentsDiff;
    }

    private UploadTrackDTO getUploadData(Track track) {
        List<Member> members = dataRepository.getChangedMembers(track.getExternalId());//dataRepository.getMembers(track.getExternalId());

        List<UploadMemberDTO> result = new ArrayList<>();

        for (Member member : members) {
            Visit visit = dataRepository.getVisit(member.getId(), LocalDate.now().toDate());

            if (visit != null) {
                MilkParam milkParams = dataRepository.getMilkParams(visit.getId());
                List<ServiceDemand> demandServices = dataRepository.getDemandServices(visit.getId());
                List<Document> documents = dataRepository.getDocuments(visit.getId());

                result.add(convertMember(
                        member, visit, milkParams, demandServices, documents));
            } else {
                result.add(convertMember(member));

            }
        }
        return new UploadTrackDTO(track.getExternalId(), track.getName(), track.getDate(), result);
    }

    private void updateDb(DownloadResponse response) {
        DownloadTrackDTO track = response.getTrack();

        dataRepository.saveServiceCodes(convertServiceCodes(track.getServiceCodes()));
        dataRepository.saveDocumentCodes(convertDocumentCodes(track.getDocumentCodes()));

        dataRepository.insertTrackInfo(convertTrack(track));
        dataRepository.insertTrackMembers(track.getId(), convertMembers(track.getMembers()));

        dataRepository.insertMemberStats(convertMemberStats(track.getMembers()));
        dataRepository.insertMilkStats(convertMilkStats(track.getMembers()));
        dataRepository.insertDocsStats(convertDocsStats(track.getMembers()));

        dataRepository.saveServices(convertServicesDelivery(
                track.getMembers(),
                dataRepository.getTrackMembers(track.getId())));

        dataRepository.deleteVisitsByPeriod();
    }

    private void clearDocuments(List<UploadedDocument> uploadedDocuments) {
        for (UploadedDocument doc : uploadedDocuments) {
            String fullDocName = doc.getName();
            String[] parts = fullDocName.substring(0, fullDocName.length() - 1).split(DELIMITER);

            File uploadedDoc = getDocumentFile(parts[1], parts[2]);
            if (uploadedDoc != null && uploadedDoc.exists()) uploadedDoc.delete();
        }
    }

    private File getDocumentFile(String memberId, String documentId) {
        File directory = getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        if (!directory.exists()) return null;

        File dir = new File(directory.getPath() + File.separator
                + ROOT_DIR + File.separator
                + memberId +  File.separator
                + documentId + ".jpg");
        return dir.exists() ? dir : null;
    }
}
