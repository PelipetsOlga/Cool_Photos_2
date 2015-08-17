package com.mobapply.happymoments.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;


public class PictureProvider extends ContentProvider {


    private static final String DB_NAME = "happymoments";
    private static final int DB_VERSION = 1;

    public static final String ALBUM_TABLE = "album";

    public static final String ALBUM_ID = "_id";
    public static final String ALBUM_NAME = "name";
    public static final String ALBUM_COUNT = "count";
    public static final String ALBUM_FOLDER = "folder";
    public static final String ALBUM_FILE = "file_name";
    public static final String ALBUM_FILE_PREVIEW = "file_preview_name";
    public static final String ALBUM_IS_PLAY = "is_play";
    public static final String ALBUM_DATE = "date";

    public static final String PICTURE_TABLE = "picture";

    public static final String PICTURE_ID = "_id";
    public static final String PICTURE_ALBUM_ID = "album_id";
    public static final String PICTURE_FILE = "file_name";
    public static final String PICTURE_FILE_PREVIEW = "file_preview_name";
    public static final String PICTURE_IS_MAIN = "is_main";
    public static final String PICTURE_IS_PLAY = "is_play";
    public static final String PICTURE_DATE = "date";

    public static final int PLAY=1;
    public static final int PlAY_NOT=0;
    public static final int MAIN=1;
    public static final int NOT_MAIN=0;


    private static final String CREATE_TABLE_ALBUM = "create table " + ALBUM_TABLE + "("
            + ALBUM_ID + " integer primary key autoincrement, "
            + ALBUM_NAME + " text, "
            + ALBUM_COUNT + " integer, "
            + ALBUM_FOLDER + " text, "
            + ALBUM_FILE + " text, "
            + ALBUM_FILE_PREVIEW + " text, "
            + ALBUM_IS_PLAY + " integer, "
            + ALBUM_DATE + " integer"
            + ");";

    private static final String CREATE_TABLE_PICTURE = "create table " + PICTURE_TABLE + "("
            + PICTURE_ID + " integer primary key autoincrement, "
            + PICTURE_ALBUM_ID + " integer, "
            + PICTURE_FILE + " text, "
            + PICTURE_FILE_PREVIEW + " text, "
            + PICTURE_IS_MAIN + " integer, "
            + PICTURE_IS_PLAY + " integer, "
            + PICTURE_DATE + " integer"
            + ");";

    private static final String AUTHORITY = "com.mobapply.happymoments.providers.HappyMoments";

    private static final String ALBUM_PATH = "albums";
    private static final String PICTURE_PATH = "pictures";

    public static final Uri ALBUM_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ALBUM_PATH);
    public static final Uri PICTURE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PICTURE_PATH);

    private static final String ALBUM_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + ALBUM_PATH;
    private static final String PICTURE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PICTURE_PATH;

    private static final String ALBUM_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + ALBUM_PATH;
    private static final String PICTURE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PICTURE_PATH;

    private static final int URI_ALBUMS = 1;
    private static final int URI_ALBUMS_ID = 2;

    private static final int URI_PICTURES = 3;
    private static final int URI_PICTURES_ID = 4;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, ALBUM_PATH, URI_ALBUMS);
        uriMatcher.addURI(AUTHORITY, ALBUM_PATH + "/#", URI_ALBUMS_ID);
        uriMatcher.addURI(AUTHORITY, PICTURE_PATH, URI_PICTURES);
        uriMatcher.addURI(AUTHORITY, PICTURE_PATH + "/#", URI_PICTURES_ID);
    }

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = dbHelper.getWritableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case URI_ALBUMS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ALBUM_ID + " ASC";
                }
                cursor = db.query(ALBUM_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(),ALBUM_CONTENT_URI);
                break;
            case URI_ALBUMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ALBUM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ALBUM_ID + " = " + id;
                }
                cursor = db.query(ALBUM_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(),ALBUM_CONTENT_URI);
                break;
            case URI_PICTURES:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = PICTURE_DATE + " DESC";
                }
                cursor = db.query(PICTURE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(),PICTURE_CONTENT_URI);
                break;
            case URI_PICTURES_ID:
                String pictureId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = PICTURE_ID + " = " + pictureId;
                } else {
                    selection = selection + " AND " + PICTURE_ID + " = " + pictureId;
                }
                cursor = db.query(PICTURE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(),PICTURE_CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_ALBUMS && uriMatcher.match(uri) != URI_PICTURES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        Uri resultUri = null;
        long rowID = 0;
        switch (uriMatcher.match(uri)) {
            case URI_ALBUMS:
                rowID = db.insert(ALBUM_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, rowID);
                break;
            case URI_PICTURES:
                rowID = db.insert(PICTURE_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(PICTURE_CONTENT_URI, rowID);
                break;
        }

        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int cnt = 0;
        switch (uriMatcher.match(uri)) {
            case URI_ALBUMS:
                cnt = db.delete(ALBUM_TABLE, selection, selectionArgs);
                break;
            case URI_ALBUMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ALBUM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ALBUM_ID + " = " + id;
                }
                cnt = db.delete(ALBUM_TABLE, selection, selectionArgs);
                break;
            case URI_PICTURES:
                cnt = db.delete(PICTURE_TABLE, selection, selectionArgs);
                break;
            case URI_PICTURES_ID:
                String pictureId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = PICTURE_ID + " = " + pictureId;
                } else {
                    selection = selection + " AND " + PICTURE_ID + " = " + pictureId;
                }
                cnt = db.delete(PICTURE_TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int cnt = 0;
        switch (uriMatcher.match(uri)) {
            case URI_ALBUMS:
                cnt = db.update(ALBUM_TABLE, values, selection, selectionArgs);
                break;
            case URI_ALBUMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ALBUM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ALBUM_ID + " = " + id;
                }
                cnt = db.update(ALBUM_TABLE, values, selection, selectionArgs);
                break;
            case URI_PICTURES:
                cnt = db.update(PICTURE_TABLE, values, selection, selectionArgs);
                break;
            case URI_PICTURES_ID:
                String pictureId = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = PICTURE_ID + " = " + pictureId;
                } else {
                    selection = selection + " AND " + PICTURE_ID + " = " + pictureId;
                }
                cnt = db.update(PICTURE_TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }


    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALBUMS:
                return ALBUM_CONTENT_TYPE;
            case URI_ALBUMS_ID:
                return ALBUM_CONTENT_ITEM_TYPE;
            case URI_PICTURES:
                return PICTURE_CONTENT_TYPE;
            case URI_PICTURES_ID:
                return PICTURE_CONTENT_ITEM_TYPE;
        }
        return null;
    }


    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ALBUM);
            db.execSQL(CREATE_TABLE_PICTURE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  }
    }
}
