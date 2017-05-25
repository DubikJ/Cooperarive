package com.avatlantik.cooperative.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.adapter.DocumentsViewAdapter;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.model.DocumentItem;
import com.avatlantik.cooperative.model.db.DocumentCode;
import com.avatlantik.cooperative.model.db.DocumentStats;
import com.avatlantik.cooperative.model.db.Member;
import com.avatlantik.cooperative.repository.DataRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.avatlantik.cooperative.common.Consts.ROOT_DIR;

public class DocumentsActivity extends AppCompatActivity {
    private static final String TAG = DocumentsActivity.class.getSimpleName();
    private ProgressBar mProgressBar;
    private DocumentsViewAdapter mGridAdapter;
    private ArrayList<DocumentItem> mGridData;
    private Member member;

    @Inject
    DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_list);
        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        GridView mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new DocumentsViewAdapter(this, R.layout.document_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        getSupportActionBar().setTitle(getString(R.string.photo_documents));

        member = (Member) getIntent().getExtras().get("member");

        //Grid view click event
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                DocumentItem item = (DocumentItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(DocumentsActivity.this, DocumentDetailsActivity.class);
                ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_image);

                // Interesting data to pass across are the thumbnail size/location, the
                // resourceId of the source bitmap, the picture description, and the
                // orientation (to avoid returning back to an obsolete configuration if
                // the device rotates again in the meantime)

                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("title", item.getTitle()).
                        putExtra("isExist", item.isExist()).
                        putExtra("uri", item.getFile()).
                        putExtra("memberExternalId", member.getExternalId());

                //Start details activity
                startActivity(intent);
            }
        });

        //Start download
        updateDocuments();

        Button retDocButton = (Button) findViewById(R.id.ret_document_button);
        retDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDocuments();
    }

    private void updateDocuments() {

        if (member != null) {
            new AsyncDocumentLoader().execute(member.getExternalId());
        }
        mProgressBar.setVisibility(View.VISIBLE);
    }

    //Downloading data asynchronously
    class AsyncDocumentLoader extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String memberId = params[0];

            List<DocumentCode> allDocumentCodes = dataRepository.getDocumentCodes();
            Set<String> existedDocs = getCodeOfExistedDocuments(memberId);

            mGridData.clear();
            for (DocumentCode doc : allDocumentCodes) {
                File docPath = getOutputPhotoFile(memberId, doc.getExternalId());
                boolean isExist = existedDocs.contains(doc.getExternalId());
                if (!isExist) mGridData.add(new DocumentItem(doc.getName(), docPath, isExist));
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Lets update UI
            if (result == 1) {
                if (mGridData.isEmpty()) {
                    Toast.makeText(DocumentsActivity.this, R.string.all_documents_exist, Toast.LENGTH_LONG).show();
                } else {
                    mGridAdapter.setGridData(mGridData);
                }
            } else {
                Toast.makeText(DocumentsActivity.this, R.string.failed_document_downloading, Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }

        Set<String> getCodeOfExistedDocuments(String memberId) {
            List<DocumentStats> memberDocs = dataRepository.getDocStatsByMemberId(memberId);
            if (memberDocs == null || memberDocs.isEmpty()) return Collections.emptySet();

            Set<String> codeList = new HashSet<>();
            for (DocumentStats docStat : memberDocs) {
                if (docStat.isValue()) codeList.add(docStat.getCode());
            }

            return codeList;
        }
    }

    private File getOutputPhotoFile(String memberId, String documentId) {
        File directory = getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }

        File dir = new File(directory.getPath() + File.separator
                + ROOT_DIR + File.separator
                + memberId);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, File.separator + documentId + ".jpg");
    }
}
