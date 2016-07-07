package mdl.sinlov.android.download;

import android.app.DownloadManager;

/**
 * mdl download listener
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
 * Created by "sinlov" on 16/7/4.
 */
public interface OnDownloadListener {
    /**
     * call back at status {@link DownloadManager#STATUS_RUNNING} or {@link DownloadManager#STATUS_PAUSED}
     *
     * @param downloadId downloadID
     * @param status     status
     */
    void downloading(long downloadId, long status, MDLDownLoadInfo mdlDownLoadInfo);

    /**
     * call back at status {@link DownloadManager#STATUS_SUCCESSFUL} and only call back once!
     *
     * @param downloadId      downloadID
     * @param mdlDownLoadInfo mdlDownLoadInfo
     */
    void downloadComplete(long downloadId, MDLDownLoadInfo mdlDownLoadInfo);

    /**
     * this error code from {@link DownloadManager#ERROR_UNKNOWN} and others code.
     *
     * @param downloadId download id
     * @param errorCode  error code
     */
    void downloadError(long downloadId, int errorCode);

    /**
     * call back at status {@link DownloadManager#STATUS_SUCCESSFUL}
     *
     * @param downloadId  downloadID
     * @param downloadUri downloadUri
     */
    void downloadHistory(long downloadId, String downloadUri);
}
