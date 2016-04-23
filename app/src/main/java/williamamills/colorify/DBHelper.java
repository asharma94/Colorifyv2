package williamamills.colorify;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mInstance;

    public static final String DATABASE_NAME = "pictures.db";
    public static final String TABLE_NAME = "photos";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PICTURE= "pic";
    public static final String COLUMN_TAG = "tag";


    private static final int DATABASE_VERSION = 1;

    private Context mCtx;


   // private static final String DATABASE_ALTER_START_TIME = "ALTER TABLE "
   //         + TABLE_NAME + " ADD COLUMN " + SITE_COLUMN_START_TIME + " text;";


    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+TABLE_NAME+" " +
                        "(id integer primary key, "+COLUMN_PICTURE+" text, "+COLUMN_TAG+" text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if(oldVersion<2){
            //db.execSQL(DATABASE_ALTER_START_TIME);
        }
    }

    public SQLiteDatabase returnDB(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db;
    }

    public boolean insertPhoto  (JSONObject picture, String tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PICTURE, picture.toString());
        contentValues.put(COLUMN_TAG, tag);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+TABLE_NAME+" where id=" + id + "", null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updatePhoto (Integer id,  JSONArray picture, String tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PICTURE, picture.toString());
        contentValues.put(COLUMN_TAG, tag);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deletePhoto (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllPhotos()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
          String s = res.getString(res.getColumnIndex(COLUMN_PICTURE)) + " " + res.getString(res.getColumnIndex(COLUMN_TAG));
            res.moveToNext();
        }
        return array_list;
    }


    public JSONObject getPhoto(int id)
    {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from " + TABLE_NAME + " where id=" + id + "", null);
        res.moveToPosition(0);
        String jsonString = res.getString(res.getColumnIndex(COLUMN_PICTURE));
        res.close();
        JSONObject j;
        try {
            j = new JSONObject(jsonString);
        }catch(Exception e){
            j = null;
        }
        //TODO get JSON to Photo
        return j;
    }

}
