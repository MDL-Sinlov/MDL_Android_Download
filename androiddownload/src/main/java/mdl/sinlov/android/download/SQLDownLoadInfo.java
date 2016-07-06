package mdl.sinlov.android.download;

/**
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
public class SQLDownLoadInfo {
    private String downloadID;
    private long downloadStatus;
    private String taskID;
    private String url;
    private String filePath;
    private long fileSize;
    private long downloadSize;

    public long getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(long downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(String downloadID) {
        this.downloadID = downloadID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    @Override
    public String toString() {
        return "SQLDownLoadInfo{" +
                "downloadID='" + downloadID + '\'' +
                ", downloadStatus=" + downloadStatus +
                ", taskID='" + taskID + '\'' +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", downloadSize=" + downloadSize +
                '}';
    }
}
