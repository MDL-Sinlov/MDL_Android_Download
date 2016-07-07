package mdl.sinlov.android.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * callback of download
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
 * Created by "sinlov" on 16/7/1.
 */
/*package*/ class MDLDownloadCallBack {

    public static final int DEFAULT_PRIORITY = 1000;
    public static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";
    private static final String INPUT_PATH_ERROR = "Your path error ";
    private Context context;
    private OnDownloadListener onDownloadListener;
    private MDLDownloadManager mdlDownloadManager;
    private ArrayList<Long> downloadIds;
    private MDLCompleteDownloadReceiver mdlCompleteDownloadReceiver;
    private final IntentFilter downloadIntentFilter;
    private final DownloadChangeObserver downloadChangeObserver;
    private final DataKeeper dataKeeper;

    private MyHandler handler = new MyHandler(this);


    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            for (int i = 0; i < downloadIds.size(); i++) {
                Long downloadId = downloadIds.get(i);
                int errorCode = mdlDownloadManager.getErrorCode(downloadId);
                long[] bytesAndStatus = mdlDownloadManager.getBytesAndStatus(downloadId);
                if (errorCode > 0) {
                    handler.sendMessage(handler.obtainMessage(2, errorCode, 0, downloadId));
                } else {
                    handler.sendMessage(handler.obtainMessage(1, bytesAndStatus));
                }
            }
        }
    }

    public MDLDownloadCallBack(Context context, MDLDownloadManager mdlDownloadManager, OnDownloadListener onDownloadListener) {
        this.context = context.getApplicationContext();
        this.mdlDownloadManager = mdlDownloadManager;
        this.onDownloadListener = onDownloadListener;
        downloadIds = new ArrayList<>();
        downloadIntentFilter = new IntentFilter();
        downloadIntentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadIntentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        downloadIntentFilter.setPriority(DEFAULT_PRIORITY);
        this.mdlCompleteDownloadReceiver = new MDLCompleteDownloadReceiver();
        this.downloadChangeObserver = new DownloadChangeObserver();
        this.dataKeeper = new DataKeeper(context);
    }

    public void start() {
        context.registerReceiver(mdlCompleteDownloadReceiver, downloadIntentFilter);
        context.getContentResolver().registerContentObserver(MDLDownloadManager.CONTENT_URI, true, downloadChangeObserver);
        observedDownloadIdsFromDB();
    }

    public void stop() {
        context.unregisterReceiver(mdlCompleteDownloadReceiver);
        context.getContentResolver().unregisterContentObserver(downloadChangeObserver);
    }

    public void setObservedDownloadIds(ArrayList<Long> downloadIds) {
        this.downloadIds = downloadIds;
    }

    public void addObservedDownloadId(long downloadID) {
        downloadIds.add(downloadID);
    }

    private void observedDownloadIdsFromDB() {
        downloadIds.clear();
        downloadIds.addAll(dataKeeper.getAllDownloadIds());
    }

    public void removeObservedDownloadId(long downloadID) {
        downloadIds.remove(downloadID);
    }

    public void clearObservedDownloadIds() {
        downloadIds.clear();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }

    public class MDLCompleteDownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (null != onDownloadListener) {
                for (long downloadId : downloadIds
                        ) {
                    if (completeDownloadId == downloadId && mdlDownloadManager.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                        onDownloadListener.downloadComplete(completeDownloadId, dataKeeper.getDownLoadInfo(String.valueOf(completeDownloadId)));
                    }
                }
            } else {
                new NullPointerException("you are not setting OnDownloadListener").printStackTrace();
            }
        }
    }

    private String queryPathByDownloadID(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = mdlDownloadManager.getDownloadManager().query(query);
        int columnCount = cursor.getColumnCount();
        String path = null;
        while (cursor.moveToNext()) {
            for (int j = 0; j < columnCount; j++) {
                String columnName = cursor.getColumnName(j);
                String string = cursor.getString(j);
                if (columnName.equals("local_uri")) {
                    path = string;
                }
            }
        }
        cursor.close();
        return path;
    }

    private static class MyHandler extends Handler {
        private static WeakReference<MDLDownloadCallBack> wkMDLDownloadCallback;

        public MyHandler(MDLDownloadCallBack downloadCallBack) {
            MyHandler.wkMDLDownloadCallback = new WeakReference<MDLDownloadCallBack>(downloadCallBack);
        }

        public WeakReference<MDLDownloadCallBack> get() {
            return wkMDLDownloadCallback;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MDLDownloadCallBack callback = wkMDLDownloadCallback.get();
            if (null != callback && null != callback.onDownloadListener && null != msg.obj) {
                switch (msg.what) {
                    case 1:
                        long[] bytesAndStatus = (long[]) msg.obj;
                        if (callback.isDownloading(bytesAndStatus[3])) {
                            if (bytesAndStatus[3] < 0) {
                                callback.updateDownloadData(bytesAndStatus[0], "", bytesAndStatus[2], 0, bytesAndStatus[3]);
                            } else {
                                String downloadUri = callback.queryPathByDownloadID(bytesAndStatus[0]);
                                callback.updateDownloadData(bytesAndStatus[0], downloadUri, bytesAndStatus[2], bytesAndStatus[1], bytesAndStatus[3]);
                            }
                        } else {
                            String downloadUri = callback.queryPathByDownloadID(bytesAndStatus[0]);
                            if (null != downloadUri) {
                                callback.updateDownloadData(bytesAndStatus[0], downloadUri, bytesAndStatus[2], bytesAndStatus[1], bytesAndStatus[3]);
                                callback.onDownloadListener.downloadHistory(bytesAndStatus[0], downloadUri);
                            } else {
                                callback.dataKeeper.deleteDownLoadInfo(String.valueOf(bytesAndStatus[0]));
                            }
                        }
                        break;
                    case 2:
                        callback.onDownloadListener.downloadError((Long) msg.obj, msg.arg1);
                        break;
                    case 10:
                        ArrayList<MDLDownLoadInfo> downloadList = callback.dataKeeper.getAllDownLoadInfo();
                        for (int i = 0; i < downloadList.size(); i++) {
                            String downloadID = downloadList.get(i).getDownloadID();
                            callback.mdlDownloadManager.getDownloadManager().remove(Long.parseLong(downloadID));
                        }
                        callback.dataKeeper.deleteAllDownLoadInfo();
                        callback.deleteDirectory((String) msg.obj);
                        break;
                    default:
                        // empty call back
                        break;
                }

            }
        }

    }

    private boolean isDownloading(long downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }

    public void saveDownloadData(long downloadID, String url) {
        MDLDownLoadInfo downloadInfo = new MDLDownLoadInfo();
        downloadInfo.setUrl(url);
        downloadInfo.setDownloadID(String.valueOf(downloadID));
        dataKeeper.saveDownLoadInfo(downloadInfo);
    }

    public void updateDownloadData(long downloadID, String subPath, long fileSize, long downloadSize, long downloadStatus) {
        MDLDownLoadInfo downloadInfo = new MDLDownLoadInfo();
        downloadInfo.setDownloadID(String.valueOf(downloadID));
        downloadInfo.setFilePath(subPath);
        downloadInfo.setFileSize(fileSize);
        downloadInfo.setDownloadStatus(downloadStatus);
        downloadInfo.setDownloadSize(downloadSize);
        dataKeeper.saveDownLoadInfo(downloadInfo);
        if (isDownloading(downloadStatus)) {
            onDownloadListener.downloading(downloadID, downloadStatus, downloadInfo);
        }
    }

    public ArrayList<MDLDownLoadInfo> getDownloadInfoByDB() {
        return dataKeeper.getAllDownLoadInfo();
    }


    public void removeDownloadData(long[] downloadID) {
        for (int i = 0; i < downloadID.length; i++) {
            dataKeeper.deleteDownLoadInfo(String.valueOf(downloadID[i]));
        }
    }

    public void clearAllDownload(String downloadFolder) {
        handler.sendMessage(handler.obtainMessage(10, downloadFolder));
    }

    private boolean deleteDirectory(String path) {
        checkInputPath(path);
        File dirFile = new File(path);
        return deleteDirectoryInnerFiles(path) && dirFile.delete();
    }

    private boolean deleteDirectoryInnerFiles(String path) {
        checkInputPath(path);
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        try {
            File[] files = dirFile.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (deleteFile(file.getAbsolutePath())) break;
                    } else {
                        if (deleteDirectory(file.getAbsolutePath())) break;
                    }
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    private boolean deleteFile(String path) {
        checkInputPath(path);
        File file = new File(path);
        return file.isFile() && file.exists() && file.delete();
    }

    private void checkInputPath(String path) {
        if (path.matches(matches)) {
            new Throwable(INPUT_PATH_ERROR).printStackTrace();
        }
    }
}
