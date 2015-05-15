package com.zr.sanhua;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by xia on 2015/5/13.
 */
public class OrderProvider extends ContentProvider {

    /* 数据库版本和名字 */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "deliver_order.db";
    public static final String AUTHORITY = "com.zr.sanhua.deliver.order.provider";

    // 数据库的表命名
    public static final String ORDER_TABLE = "order_table";// 最新订单表
    public static final String GOOD_TABLE = "good_table";// 商品信息表

    // URI配置
    public static final Uri ORDER_URI = Uri.parse("content://" + AUTHORITY
            + "/" + ORDER_TABLE);
    public static final Uri GOOD_URI = Uri.parse("content://" + AUTHORITY + "/"
            + GOOD_TABLE);


    // 最新订单表
    public static final String DELIVER_ID = "deliver_id";// 关联查询订单表
    public static final String ORDER_ID = "order_id";// 订单id，是订单表的主键，关联查询语音记录表和商品信息表
    public static final String ORDER_STATE = "order_state";// 订单状态
    public static final String ADRESS = "adress";// 地址
    public static final String PHONE = "phone";// 电话
    public static final String TOTAL_PRICE = "phone_food_price";// 总价格
    public static final String DELIVER_PRICE = "deliver_price";// 运费
    public static final String REMARKS = "remarks";// 备注描述

    // 商品表
    public static final String GOOD_ID = "good_id";
    public static final String GOOD_NAME = "good_name";
    public static final String GOOD_PRICE = "good_price";
    public static final String GOOD_NUM = "good_num";


    public static final String[] ORDER_PROJECTION = new String[]{DELIVER_ID,
            ORDER_ID, ORDER_STATE, ADRESS, PHONE, TOTAL_PRICE, DELIVER_PRICE, REMARKS};

    public static final String[] GOOD_PROJECTION = new String[]{ORDER_ID,
            GOOD_ID, GOOD_NAME, GOOD_PRICE, GOOD_NUM};

    private DatabaseHelper mOpenHelper;

    private static final UriMatcher sUriMatcher;

    private static final int ODER_URIMACHER = 1;
    private static final int GOOD_URIMACHER = 2;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, ORDER_TABLE, ODER_URIMACHER);
        sUriMatcher.addURI(AUTHORITY, GOOD_TABLE, GOOD_URIMACHER);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        String table;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {

            case ODER_URIMACHER:
                table = ORDER_TABLE;
                count = db.delete(table, selection, selectionArgs);
                break;
            case GOOD_URIMACHER:
                table = GOOD_TABLE;
                count = db.delete(table, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table;
        long rowId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case ODER_URIMACHER:
                table = ORDER_TABLE;
                rowId = db.insert(table, null, values);
                break;
            case GOOD_URIMACHER:
                table = GOOD_TABLE;
                rowId = db.insert(table, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (rowId > 0) {
            return uri;
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table;
        switch (sUriMatcher.match(uri)) {
            case ODER_URIMACHER:
                table = ORDER_TABLE;
                break;
            case GOOD_URIMACHER:
                table = GOOD_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor c = db.query(table, projection, selection, selectionArgs, null,
                null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        String table;
        switch (sUriMatcher.match(uri)) {
            case ODER_URIMACHER:
                table = ORDER_TABLE;
                break;
            case GOOD_URIMACHER:
                table = GOOD_TABLE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(table, values, selection, selectionArgs);

    }

    @Override
    public ContentProviderResult[] applyBatch(
            @NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public static final String TAG = "OrderProvider";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.e(TAG, "create table start");

            db.execSQL("CREATE TABLE " + ORDER_TABLE + " (" + ORDER_ID
                    + " INTEGER PRIMARY KEY," + DELIVER_ID + " INTEGER,"
                    + ORDER_STATE + " INTEGER," + PHONE + " TEXT," + ADRESS
                    + " TEXT," + TOTAL_PRICE + " REAL," + DELIVER_PRICE + " REAL,"
                    + REMARKS + " TEXT);");


            db.execSQL("CREATE TABLE " + GOOD_TABLE + " (" + GOOD_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + ORDER_ID
                    + " INTEGER," + GOOD_NAME + " TEXT," + GOOD_PRICE
                    + " REAL," + GOOD_NUM + " INTEGER);");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //数据库版本变化，表重新创建
            db.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + GOOD_TABLE);
            onCreate(db);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
