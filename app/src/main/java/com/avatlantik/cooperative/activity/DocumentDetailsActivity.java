package com.avatlantik.cooperative.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avatlantik.cooperative.R;
import com.avatlantik.cooperative.app.CooperativeApplication;
import com.avatlantik.cooperative.repository.DataRepository;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

public class DocumentDetailsActivity extends AppCompatActivity {
    @Inject
    DataRepository dataRepository;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 999;
    private static final int ANIM_DURATION = 600;
    private Drawable colorDrawable;
    private ImageView imageView;
    Uri path = null;

    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private int thumbnailTop;
    private int thumbnailLeft;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private String memberExternalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_details);

        ((CooperativeApplication) getApplication()).getComponent().inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //retrieves the thumbnail data
        final Bundle bundle = getIntent().getExtras();
        thumbnailTop = bundle.getInt("top");
        thumbnailLeft = bundle.getInt("left");
        thumbnailWidth = bundle.getInt("width");
        thumbnailHeight = bundle.getInt("height");
        memberExternalId = bundle.getString("memberExternalId");

        File sourceFile = (File) bundle.get("uri");
        path = Uri.fromFile(sourceFile);

        //initialize and set the image description
        TextView titleTextView = (TextView) findViewById(R.id.grid_item_title_detils);
        titleTextView.setText(bundle.getString("title"));

        Button newPhotoButton = (Button) findViewById(R.id.make_photo);
        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(path.getPath());
                if (file.exists()) file.delete();

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, path);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQ);
            }
        });

        Button saveButton = (Button) findViewById(R.id.save_document_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRepository.setMemberChanged(memberExternalId);
                DocumentDetailsActivity.this.onBackPressed();
            }
        });

        Button retDocDetButton = (Button) findViewById(R.id.ret_doc_detail_button);
        retDocDetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = (ImageView) findViewById(R.id.grid_item_image_details);
        Picasso.with(this).load(path).placeholder(R.drawable.shape_camera).into(imageView);

        Boolean isExistDocument = bundle.getBoolean("isExist");
        if (sourceFile != null && !sourceFile.exists() && isExistDocument) {
            Toast.makeText(this, R.string.document_already_exists, Toast.LENGTH_LONG).show();
        }

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_background);
        colorDrawable = getResources().getDrawable(R.drawable.background_activity);
        frameLayout.setBackground(colorDrawable);

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        if (savedInstanceState == null) {
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    imageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / imageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / imageView.getHeight();

                    enterAnimation();

                    return true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data == null ? path : data.getData();
                Log.d("DOCUMENT_CAPTURE", "Image saved successfully to " + photoUri.getPath());
                Picasso.with(this).load(photoUri).placeholder(R.drawable.shape_camera).into(imageView);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("DOCUMENT_CAPTURE", "Cancelled");
            }
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location.
     */
    public void enterAnimation() {

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        imageView.setPivotX(0);
        imageView.setPivotY(0);
        imageView.setScaleX(mWidthScale);
        imageView.setScaleY(mHeightScale);
        imageView.setTranslationX(mLeftDelta);
        imageView.setTranslationY(mTopDelta);

        // interpolator where the rate of change starts out quickly and then decelerates.
        TimeInterpolator sDecelerator = new DecelerateInterpolator();

        // Animate scale and translation to go from thumbnail to full size
        imageView.animate().setDuration(ANIM_DURATION).scaleX(1).scaleY(1).
                translationX(0).translationY(0).setInterpolator(sDecelerator);

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0, 255);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    /**
     * The exit animation is basically a reverse of the enter animation.
     * This Animate image back to thumbnail size/location as relieved from bundle.
     *
     * @param endAction This action gets run after the animation completes (this is
     *                  when we actually switch activities)
     */
    public void exitAnimation(final Runnable endAction) {

        TimeInterpolator sInterpolator = new AccelerateInterpolator();
        imageView.animate().setDuration(ANIM_DURATION).scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta)
                .setInterpolator(sInterpolator).withEndAction(endAction);

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(colorDrawable, "alpha", 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    @Override
    public void onBackPressed() {
        exitAnimation(new Runnable() {
            public void run() {
                finish();
            }
        });
    }
}
