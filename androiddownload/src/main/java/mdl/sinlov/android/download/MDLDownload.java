package mdl.sinlov.android.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;

/**
 * mdl download utils
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by "sinlov" on 16/6/30.
 */
public class MDLDownload {

    private final MDLDownloadCallBack mdlDownloadCallBack;
    private Context context;
    private DownloadManager downloadManager;
    private MDLDownloadManager mdlDownloadManager;
    private String downloadFolder;
    private boolean isVisibleInDownloadUI = true;


    /**
     * get Download setting absolute path, if this device has not sdcard, it will return null
     *
     * @return {@link String}
     */
    @SuppressWarnings("ConstantConditions")
    public String getDownloadFolder() {
        return context.getExternalFilesDir(downloadFolder).getAbsolutePath();
    }

    /**
     * set visible download ui
     *
     * @param visibleInDownloadUI boolean
     */
    public void setVisibleInDownloadUI(boolean visibleInDownloadUI) {
        isVisibleInDownloadUI = visibleInDownloadUI;
    }

    /**
     * get Full download Info by DB, it will cost some time.
     *
     * @return {@link ArrayList} with {@link SQLDownLoadInfo}
     */
    public ArrayList<SQLDownLoadInfo> getDownloadInfoByDB() {
        return mdlDownloadCallBack.getDownloadInfoByDB();
    }

    /**
     * submit download task, url will not check so, return at {@link OnDownloadListener#downloadError(long, int)}
     * <ul>
     * <li>saveName will change by the same name</li>
     * <li>isMobileDownload will let use Mobile Download</li>
     * </ul>
     *
     * @param url              {@link String} url of download
     * @param saveName         {@link String}
     * @param isMobileDownload boolean
     * @return downloadID
     */
    public long submitDownload(String url, String saveName, boolean isMobileDownload) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(context, downloadFolder, saveName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(isVisibleInDownloadUI);
        if (isVisibleInDownloadUI) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        } else {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        if (isMobileDownload) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        } else {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
        long downloadID = downloadManager.enqueue(request);
        mdlDownloadCallBack.addObservedDownloadId(downloadID);
        mdlDownloadCallBack.saveDownloadData(downloadID, url);
        return downloadID;
    }

    /**
     * pause download by id, if ROM can't support, will print {@link NoSuchMethodError}
     *
     * @param downloadID long...
     */
    public void pauseDownload(long... downloadID) {
        if (MDLDownloadManager.isExistPauseAndResumeMethod()) {
            mdlDownloadManager.pauseDownload(downloadID);
        } else {
            new NoSuchMethodError("can not pause download at this rom").printStackTrace();
        }
    }

    /**
     * resume download by id, if ROM can't support, will print {@link NoSuchMethodError}
     *
     * @param downloadID long...
     */
    public void resumeDownload(long... downloadID) {
        if (MDLDownloadManager.isExistPauseAndResumeMethod()) {
            mdlDownloadManager.resumeDownload(downloadID);
        } else {
            new NoSuchMethodError("can not resume download at this rom").printStackTrace();
        }
    }

    /**
     * remove download by id
     *
     * @param downloadID long...
     */
    public void removeDownload(long... downloadID) {
        downloadManager.remove(downloadID);
        mdlDownloadCallBack.removeDownloadData(downloadID);
    }

    /**
     * clean all download catch and download info at db!
     */
    public void cleanAllDownload() {
        mdlDownloadCallBack.clearAllDownload(getDownloadFolder());
    }

    /**
     * bind callback, most use at {@link Activity#onResume()}
     */
    public void bind() {
        mdlDownloadCallBack.start();
    }

    /**
     * unBind callback, most use at {@link Activity#onPause()}
     */
    public void unBind() {
        mdlDownloadCallBack.stop();
    }

    public MDLDownloadManager getMdlDownloadManager() {
        return mdlDownloadManager;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /**
     * when use download, you must give {@link Context} and {@link String} downloadFolder
     * <br> Download folder can use {@link #getDownloadFolder()}
     *
     * @param context            {@link Context}
     * @param downloadFolder     {@link String}
     * @param onDownloadListener {@link OnDownloadListener}
     */
    public MDLDownload(Context context, String downloadFolder, OnDownloadListener onDownloadListener) {
        this.context = context.getApplicationContext();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mdlDownloadManager = new MDLDownloadManager(context);
        this.downloadFolder = downloadFolder;
        new SQLiteHelper(context);
        mdlDownloadCallBack = new MDLDownloadCallBack(context, mdlDownloadManager, onDownloadListener);
    }
}
