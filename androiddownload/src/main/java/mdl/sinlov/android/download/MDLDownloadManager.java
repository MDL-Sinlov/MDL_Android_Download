package mdl.sinlov.android.download;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * MDLDownloadManager
 * <pre>
 * Get download info<br>
 *  {@link #getStatusById(long)} get download status
 *  {@link #getDownloadBytes(long)} get downloaded byte, total byte
 *  {@link #getBytesAndStatus(long)} get downloaded byte, total byte and download status
 *  {@link #getFileName(long)} get download file name
 *  {@link #getUri(long)} get download uri
 *  {@link #getReason(long)} get failed code or paused reason
 *  {@link #getPausedReason(long)} get paused reason
 *  {@link #getErrorCode(long)} get failed error code
 * <br>Operate download<br>
 *  {@link #isExistPauseAndResumeMethod()} whether exist pauseDownload and resumeDownload method in
 * {@link DownloadManager}
 *  {@link #pauseDownload(long...)} pause download. need pauseDownload(long...) method in {@link DownloadManager}
 *  {@link #resumeDownload(long...)} resume download. need resumeDownload(long...) method in {@link DownloadManager}
 * <br>RequestPro<br>
 *  {@link RequestPro#setNotiClass(String)} set noti class
 *  {@link RequestPro#setNotiExtras(String)} set noti extras
 * </pre>
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
public class MDLDownloadManager {

    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    /**
     * represents downloaded file above api 11
     **/
    public static final String COLUMN_LOCAL_FILENAME = "local_filename";
    /**
     * represents downloaded file below api 11
     **/
    public static final String COLUMN_LOCAL_URI = "local_uri";

    public static final String METHOD_NAME_PAUSE_DOWNLOAD = "pauseDownload";
    public static final String METHOD_NAME_RESUME_DOWNLOAD = "resumeDownload";

    private static boolean isInitPauseDownload = false;
    private static boolean isInitResumeDownload = false;

    private static Method pauseDownload = null;
    private static Method resumeDownload = null;

    private DownloadManager downloadManager;

    public MDLDownloadManager(Context context) {
        this.downloadManager = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    /**
     * get download status
     *
     * @param downloadId int
     * @return int
     */
    public int getStatusById(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_STATUS);
    }

    /**
     * get downloaded byte, total byte
     *
     * @param downloadId long
     * @return a int array with two elements
     * <ul>
     * <li> result[0] represents downloaded downloadId
     * <li> result[1] represents downloaded bytes, This will initially be -1.
     * <li> result[2] represents total bytes, This will initially be -1.
     * </ul>
     */
    public long[] getDownloadBytes(long downloadId) {
        long[] bytesAndStatus = getBytesAndStatus(downloadId);
        return new long[]{downloadId, bytesAndStatus[1], bytesAndStatus[2]};
    }

    /**
     * get downloaded byte, total byte and download status
     *
     * @param downloadId long
     * @return a int array with three elements
     * <ul>
     * <li> result[0] represents downloaded downloadId.
     * <li> result[1] represents downloaded bytes, This will initially be -1.
     * <li> result[2] represents total bytes, This will initially be -1.
     * <li> result[3] represents download status, This will initially be 0.
     * </ul>
     */
    public long[] getBytesAndStatus(long downloadId) {
        long[] bytesAndStatus = new long[]{-1, -1, 0, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = downloadId;
                bytesAndStatus[1] = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                bytesAndStatus[2] = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                bytesAndStatus[3] = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * pause download
     *
     * @param ids the IDs of the downloads to be paused
     * @return the number of downloads actually paused, -1 if exception or method not exist
     */
    public int pauseDownload(long... ids) {
        initPauseMethod();
        if (pauseDownload == null) {
            return -1;
        }

        try {
            return (Integer) pauseDownload.invoke(downloadManager, ids);
        } catch (Exception e) {
            /**
             * accept all exception, include ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
             * NullPointException
             */
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * resume download
     *
     * @param ids the IDs of the downloads to be resumed
     * @return the number of downloads actually resumed, -1 if exception or method not exist
     */
    public int resumeDownload(long... ids) {
        initResumeMethod();
        if (resumeDownload == null) {
            return -1;
        }

        try {
            return (Integer) resumeDownload.invoke(downloadManager, ids);
        } catch (Exception e) {
            /**
             * accept all exception, include ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
             * NullPointException
             */
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * whether exist pauseDownload and resumeDownload method in {@link DownloadManager}
     *
     * @return boolean
     */
    public static boolean isExistPauseAndResumeMethod() {
        initPauseMethod();
        initResumeMethod();
        return pauseDownload != null && resumeDownload != null;
    }

    private static void initPauseMethod() {
        if (isInitPauseDownload) {
            return;
        }
        isInitPauseDownload = true;
        try {
            pauseDownload = DownloadManager.class.getMethod(METHOD_NAME_PAUSE_DOWNLOAD, long[].class);
        } catch (Exception e) {
            // accept all exception
            e.printStackTrace();
        }
    }

    private static void initResumeMethod() {
        if (isInitResumeDownload) {
            return;
        }
        isInitResumeDownload = true;
        try {
            resumeDownload = DownloadManager.class.getMethod(METHOD_NAME_RESUME_DOWNLOAD, long[].class);
        } catch (Exception e) {
            // accept all exception
            e.printStackTrace();
        }
    }

    /**
     * get download file name
     *
     * @param downloadId long
     * @return String
     */
    public String getFileName(long downloadId) {
        return getString(downloadId, (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? COLUMN_LOCAL_URI
                : COLUMN_LOCAL_FILENAME));
    }

    /**
     * get download uri
     *
     * @param downloadId long
     * @return String
     */
    public String getUri(long downloadId) {
        return getString(downloadId, DownloadManager.COLUMN_URI);
    }

    /**
     * get failed code or paused reason
     *
     * @param downloadId long
     * @return <ul>
     * <li> if status of downloadId is {@link DownloadManager#STATUS_PAUSED}, return
     * {@link #getPausedReason(long)}
     * <li> if status of downloadId is {@link DownloadManager#STATUS_FAILED}, return {@link #getErrorCode(long)}
     * <li> if status of downloadId is neither {@link DownloadManager#STATUS_PAUSED} nor
     * {@link DownloadManager#STATUS_FAILED}, return 0
     * </ul>
     */
    public int getReason(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get paused reason
     *
     * @param downloadId long
     * @return <ul>
     * <li> if status of downloadId is {@link DownloadManager#STATUS_PAUSED}, return one of
     * {@link DownloadManager#PAUSED_WAITING_TO_RETRY}<br>
     * {@link DownloadManager#PAUSED_WAITING_FOR_NETWORK}<br>
     * {@link DownloadManager#PAUSED_QUEUED_FOR_WIFI}<br>
     * {@link DownloadManager#PAUSED_UNKNOWN}
     * <li> else return {@link DownloadManager#PAUSED_UNKNOWN}
     * </ul>
     */
    public int getPausedReason(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    /**
     * get failed error code
     *
     * @param downloadId long
     * @return one of {@link DownloadManager}
     */
    public int getErrorCode(long downloadId) {
        return getInt(downloadId, DownloadManager.COLUMN_REASON);
    }

    public static class RequestPro extends Request {

        public static final String METHOD_NAME_SET_NOTI_CLASS = "setNotiClass";
        public static final String METHOD_NAME_SET_NOTI_EXTRAS = "setNotiExtras";

        private static boolean isInitNotiClass = false;
        private static boolean isInitNotiExtras = false;

        private static Method setNotiClass = null;
        private static Method setNotiExtras = null;

        /**
         * @param uri the HTTP URI to download.
         */
        public RequestPro(Uri uri) {
            super(uri);
        }

        /**
         * set noti class, only init once
         *
         * @param className full class name
         */
        public void setNotiClass(String className) {
            synchronized (this) {

                if (!isInitNotiClass) {
                    isInitNotiClass = true;
                    try {
                        setNotiClass = Request.class.getMethod(METHOD_NAME_SET_NOTI_CLASS, CharSequence.class);
                    } catch (Exception e) {
                        // accept all exception
                        e.printStackTrace();
                    }
                }
            }

            if (setNotiClass != null) {
                try {
                    setNotiClass.invoke(this, className);
                } catch (Exception e) {
                    /**
                     * accept all exception, include ClassNotFoundException, NoSuchMethodException,
                     * InvocationTargetException, NullPointException
                     */
                    e.printStackTrace();
                }
            }
        }

        /**
         * set noti extras, only init once
         *
         * @param extras String
         */
        public void setNotiExtras(String extras) {
            synchronized (this) {

                if (!isInitNotiExtras) {
                    isInitNotiExtras = true;
                    try {
                        setNotiExtras = Request.class.getMethod(METHOD_NAME_SET_NOTI_EXTRAS, CharSequence.class);
                    } catch (Exception e) {
                        // accept all exception
                        e.printStackTrace();
                    }
                }
            }

            if (setNotiExtras != null) {
                try {
                    setNotiExtras.invoke(this, extras);
                } catch (Exception e) {
                    /**
                     * accept all exception, include ClassNotFoundException, NoSuchMethodException,
                     * InvocationTargetException, NullPointException
                     */
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * get string column
     *
     * @param downloadId long
     * @param columnName String
     * @return String
     */
    private String getString(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        String result = null;
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getString(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * get int column
     *
     * @param downloadId long
     * @param columnName String
     * @return int
     */
    private int getInt(long downloadId, String columnName) {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        int result = -1;
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(columnName));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }
}
