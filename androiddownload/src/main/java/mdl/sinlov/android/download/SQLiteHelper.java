package mdl.sinlov.android.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

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
/*package*/ class SQLiteHelper extends SQLiteOpenHelper {

    private static final String databaseName = "mdl_file_download";
    private static CursorFactory factory = null;
    public static final String TABLE_NAME = "download_info";

    public SQLiteHelper(Context context, int dataVersion) {
        super(context, databaseName, factory, dataVersion);
    }

    public SQLiteHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String downloadSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                + "downloadID VARCHAR, "
                + "downloadStatus INTEGER, "
                + "taskID VARCHAR, "
                + "url VARCHAR, "
                + "filePath VARCHAR, "
                + "fileSize VARCHAR, "
                + "downLoadSize VARCHAR "
                + ")";
        db.execSQL(downloadSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            String downloadSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , "
                    + "downloadID VARCHAR, "
                    + "downloadStatus INTEGER, "
                    + "taskID VARCHAR, "
                    + "url VARCHAR, "
                    + "filePath VARCHAR, "
                    + "fileSize VARCHAR, "
                    + "downLoadSize VARCHAR "
                    + ")";
            db.execSQL(downloadSQL);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
