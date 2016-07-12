package mdl.sinlov.download.app.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import mdl.sinlov.android.download.MDLDownLoadInfo;
import mdl.sinlov.android.download.MDLDownload;
import mdl.sinlov.android.download.OnDownloadListener;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.download.app.R;

public class BaseDownloadActivity extends MDLTestActivity {

    public static final String DOWNLOAD_FOLDER_NAME = "1_Test_Download";
    public static final String DOWNLOAD_FILE_NAME = "TestOneDownload.apk";

    public static final String APK_URL = "http://10.8.230.124:8082/app-debug.apk";

    private FloatingActionButton fab;
    private Button btnDownloadButton;
    private Button btnDownloadCancel;
    private ProgressBar pbDownloadProgress;
    private TextView tvDownloadTip;
    private TextView tvDownloadSize;
    private TextView tvDownloadPercentage;
    private MDLDownload mdlDownload;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_download);
        initData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnDownloadButton = getViewById(R.id.download_button);
        btnDownloadCancel = getViewById(R.id.download_cancel);
        pbDownloadProgress = getViewById(R.id.download_progress);
        tvDownloadTip = getViewById(R.id.download_tip);
        String downPathInfo = getString(R.string.tip_download_file)
                + mdlDownload.getDownloadFolder();
        tvDownloadTip.setText(downPathInfo);
        tvDownloadSize = getViewById(R.id.download_size);
        tvDownloadPercentage = getViewById(R.id.download_precent);
    }

    private void initData() {
        mdlDownload = new MDLDownload(this, DOWNLOAD_FOLDER_NAME, new TestDownloadCallback(), 2);
        ArrayList<MDLDownLoadInfo> downloadInfo = mdlDownload.getDownloadInfoByDB();
        testTimeUseStart();
        for (MDLDownLoadInfo info :
                downloadInfo) {
            ALog.d(info.toString());
        }
        testTimeUseEnd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mdlDownload.bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mdlDownload.unBind();
    }

    @Override
    protected void bindListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        btnDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdlDownload.submitDownload(APK_URL, DOWNLOAD_FILE_NAME, true);
            }
        });
    }

    private class TestDownloadCallback implements OnDownloadListener {


        @Override
        public void downloading(long downloadId, long status, MDLDownLoadInfo mdlDownLoadInfo) {
            long downloadSize = mdlDownLoadInfo.getDownloadSize();
            long totalFileSize = mdlDownLoadInfo.getFileSize();
            ALog.d("downloading id: " + downloadId +
                    " status: " + status +
                    " download: " + downloadSize +
                    " total: " + totalFileSize);
            if (totalFileSize > 0) {
                tvDownloadSize.setText(String.valueOf(totalFileSize));
                tvDownloadSize.setVisibility(View.VISIBLE);
                int progress = (int) (downloadSize * 100 / totalFileSize);
                pbDownloadProgress.setProgress(progress);
                tvDownloadPercentage.setText(String.valueOf(progress + "%"));
                tvDownloadPercentage.setVisibility(View.VISIBLE);
                pbDownloadProgress.setVisibility(View.VISIBLE);
                btnDownloadCancel.setVisibility(View.VISIBLE);
                btnDownloadButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void downloadComplete(long downloadId, MDLDownLoadInfo mdlDownLoadInfo) {
            ALog.v("downloadComplete id: " + downloadId + " downloadUri: " + mdlDownLoadInfo.getFilePath());
            String showInfo = "Download Success! Size: " + mdlDownLoadInfo.getFileSize();
            tvDownloadSize.setText(showInfo);
            tvDownloadSize.setVisibility(View.VISIBLE);
            tvDownloadPercentage.setVisibility(View.GONE);
            pbDownloadProgress.setProgress(100);
            pbDownloadProgress.setVisibility(View.VISIBLE);
            btnDownloadCancel.setVisibility(View.GONE);
            btnDownloadButton.setVisibility(View.GONE);
        }

        @Override
        public void downloadError(long downloadId, int errorCode) {
            ALog.e("downloadError id: " + downloadId + " errorCode: " + errorCode);
        }

        @Override
        public void downloadHistory(long downloadId, String downloadUri) {
            ALog.i("downloadHistory id: " + downloadId + " downloadUri: " + downloadUri);
        }

        @Override
        public void downloadOutChange(long downloadId, long status) {
            ALog.w("downloadHistory id: " + downloadId + " status: " + status);
        }
    }
}
