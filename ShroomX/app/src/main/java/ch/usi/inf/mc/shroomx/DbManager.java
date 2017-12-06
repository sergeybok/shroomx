package ch.usi.inf.mc.shroomx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by sergiybokhnyak on 06.12.17.
 */

public class DbManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Mushroom.db";
    private static final String TABLE_NAME = "Mushrooms";
    private static final String _ID = "primary_key";
    private static final String NAME = "name";
    private static final String DATE = "date_found";
    private static final String IMAGE = "image";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbManager.TABLE_NAME + " (" +
                    DbManager._ID + " INTEGER PRIMARY KEY," +
                    DbManager.NAME + " TEXT," +
                    DbManager.DATE + " INTEGER, " +
                    DbManager.IMAGE + "BLOB );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbManager.TABLE_NAME;



    private static SQLiteDatabase db;



    private static  DbManager singletonDbManager;

    private DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DbManager GetDbManager(Context c) {

        if (singletonDbManager == null) {
            singletonDbManager = new DbManager(c);
            db = singletonDbManager.getWritableDatabase();
        }

        return singletonDbManager;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void onCreate(SQLiteDatabase sql) {
        sql.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase sql, int oldVersion, int newVersion) {
        deleteTable(sql);
        onCreate(sql);
    }


    private void deleteTable(SQLiteDatabase sql) {
        sql.execSQL(SQL_DELETE_ENTRIES);
    }



    public boolean addMushroom(MushroomRecord mushroom) {

        ContentValues values = new ContentValues();

        byte[] imgData = getBitmapAsByteArray(mushroom.getImg());

        values.put(NAME, mushroom.getName());
        values.put(DATE, mushroom.getDateFound().getTime());
        values.put(IMAGE, imgData);

        long newRowId = db.insert(DbManager.TABLE_NAME, null, values);

        return true;
    }


    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    private Bitmap retrieveImg(String key ) {
        String qu = "select img  from table where " + _ID + "=" + key ;
        Cursor cur = db.rawQuery(qu, null);

        if (cur.moveToFirst()){
            byte[] imgByte = cur.getBlob(0);
            cur.close();
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }
        return null;
    }


}
