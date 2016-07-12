package mdl.sinlov.android.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;


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
/*package*/ class DataKeeper {

    private static final int MAX_SAVE_TIMES = 5;
    private int doSaveTimes = 0;
    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public DataKeeper(Context context, int dataVersion) {
        this.dbHelper = new SQLiteHelper(context, dataVersion);
    }

    /**
     * save download info
     *
     * @param downloadInfo {@link MDLDownLoadInfo}
     */
    public void saveDownLoadInfo(MDLDownLoadInfo downloadInfo) {
        ContentValues cv = new ContentValues();
        cv.put("downloadID", downloadInfo.getDownloadID());
        cv.put("downloadStatus", downloadInfo.getDownloadStatus());
        String taskID = downloadInfo.getTaskID();
        taskID = checkString2Empty(taskID);
        cv.put("taskID", taskID);
        String url = downloadInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            cv.put("url", url);
        }
        String filePath = downloadInfo.getFilePath();
        filePath = checkString2Empty(filePath);
        cv.put("filePath", filePath);
        long fileSize = downloadInfo.getFileSize();
        cv.put("fileSize", fileSize);
        long downloadSize = downloadInfo.getDownloadSize();
        cv.put("downLoadSize", downloadSize);
        Cursor cursor = null;
        try {
            db = dbHelper.getWritableDatabase();
            cursor = db.rawQuery(
                    "SELECT * from " + SQLiteHelper.TABLE_NAME
                            + " WHERE downloadID = ? AND taskID = ? ", new String[]{downloadInfo.getDownloadID(), taskID});
            if (cursor.moveToNext()) {
                db.update(SQLiteHelper.TABLE_NAME, cv, "downloadID = ? AND taskID = ? ", new String[]{downloadInfo.getDownloadID(), taskID});
            } else {
                db.insert(SQLiteHelper.TABLE_NAME, null, cv);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            doSaveTimes++;
            if (doSaveTimes < MAX_SAVE_TIMES) {
                saveDownLoadInfo(downloadInfo);
            } else {
                doSaveTimes = 0;
            }
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
            e.printStackTrace();
        }
        doSaveTimes = 0;
    }

    private String checkString2Empty(String input) {
        if (TextUtils.isEmpty(input)) {
            input = "";
        }
        return input;
    }

    public MDLDownLoadInfo getDownLoadInfo(String downloadID) {
        return getDownLoadInfo(downloadID, "");
    }

    public MDLDownLoadInfo getDownLoadInfo(String downloadID, String taskID) {
        MDLDownLoadInfo downloadInfo = null;
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * from " + SQLiteHelper.TABLE_NAME
                        + " WHERE downloadID = ? AND taskID = ? ", new String[]{downloadID, taskID});
        if (cursor.moveToNext()) {
            downloadInfo = new MDLDownLoadInfo();
            downloadInfo.setDownloadID(cursor.getString(cursor.getColumnIndex("downloadID")));
            downloadInfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadInfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadInfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadInfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadInfo.setDownloadStatus(cursor.getLong(cursor.getColumnIndex("downloadStatus")));
            downloadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        }
        cursor.close();
        db.close();
        return downloadInfo;
    }

    public ArrayList<MDLDownLoadInfo> getAllDownLoadInfo() {
        ArrayList<MDLDownLoadInfo> downloadInfoList = new ArrayList<>();
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * from " + SQLiteHelper.TABLE_NAME, null);
        while (cursor.moveToNext()) {
            MDLDownLoadInfo downloadInfo = new MDLDownLoadInfo();
            downloadInfo.setDownloadID(cursor.getString(cursor.getColumnIndex("downloadID")));
            downloadInfo.setDownloadSize(cursor.getLong(cursor.getColumnIndex("downLoadSize")));
            downloadInfo.setFilePath(cursor.getString(cursor.getColumnIndex("filePath")));
            downloadInfo.setFileSize(cursor.getLong(cursor.getColumnIndex("fileSize")));
            downloadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            downloadInfo.setTaskID(cursor.getString(cursor.getColumnIndex("taskID")));
            downloadInfo.setDownloadStatus(cursor.getLong(cursor.getColumnIndex("downloadStatus")));
            downloadInfoList.add(downloadInfo);
        }
        cursor.close();
        db.close();
        return downloadInfoList;
    }

    public ArrayList<Long> getAllDownloadIds() {
        ArrayList<Long> downloadIds = new ArrayList<>();
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT * from " + SQLiteHelper.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                String downloadID = cursor.getString(cursor.getColumnIndex("downloadID"));
                downloadIds.add(Long.valueOf(downloadID));
            }
            cursor.close();
            db.close();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return downloadIds;
    }

    public void deleteDownLoadInfo(String downloadID, String taskID) {
        db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_NAME, " downloadID = ? AND taskID = ? ", new String[]{downloadID, taskID});
        db.close();
    }

    public void deleteDownLoadInfo(String downloadID) {
        deleteDownLoadInfo(downloadID, "");
    }

    public void deleteAllDownLoadInfo() {
        db = dbHelper.getWritableDatabase();
        db.delete(SQLiteHelper.TABLE_NAME, null, null);
        db.close();
    }
}
