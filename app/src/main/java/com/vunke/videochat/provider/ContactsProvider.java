package com.vunke.videochat.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vunke.videochat.db.ContactsSQLite;
import com.vunke.videochat.db.ContactsTable;
import com.vunke.videochat.receiver.CallRecordReceiver;

/**
 * Created by zhuxi on 2020/11/26.
 */

public class ContactsProvider extends ContentProvider {
    private static final String TAG = "ContactsProvider";

    private static String AUTHORITH= "com.vunke.videochat.attn";
    private static String PATH ="/attn";
    private static String PATHS ="/attn/#";
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CODE_DIR = 1;
    private static final int CODE_ITEM = 2;
    static {
        uriMatcher.addURI(AUTHORITH, PATH, CODE_DIR);
        uriMatcher.addURI(AUTHORITH, PATHS, CODE_ITEM);
    }
    private ContactsSQLite sqLite;
    private SQLiteDatabase db;
    @Override
    public boolean onCreate() {
        sqLite = new ContactsSQLite(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        db = sqLite.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(ContactsTable.INSTANCE.getTABLE_NAME(),projection,selection,selectionArgs,null,null,sortOrder,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CODE_DIR:
                return "vnd.android.cursor.dir/" + ContactsTable.INSTANCE.getTABLE_NAME();
            case CODE_ITEM:
                return "vnd.android.cursor.item/" + ContactsTable.INSTANCE.getTABLE_NAME();
            default:
                throw new IllegalArgumentException("异常参数:Unknown URI:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        db = sqLite.getWritableDatabase();
        long count=-1;
        try {
            count = db.replace(ContactsTable.INSTANCE.getTABLE_NAME(),null, values);
            getContext().getContentResolver().notifyChange(uri,null);
            Intent intent = new Intent();
            intent.setAction(CallRecordReceiver.CALL_RECORD_ACTION);
            getContext().sendBroadcast(intent);
            return  ContentUris.withAppendedId(uri, count);
        }catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        db = sqLite.getWritableDatabase();
        int number = -1;
        try {
            number = db.delete(ContactsTable.INSTANCE.getTABLE_NAME(), selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri,null);
            Intent intent = new Intent();
            intent.setAction(CallRecordReceiver.CALL_RECORD_ACTION);
            getContext().sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int number = -1;
        db = sqLite.getWritableDatabase();
        try{
            number = db.update(ContactsTable.INSTANCE.getTABLE_NAME(), values, selection, selectionArgs);
            getContext().getContentResolver().notifyChange(uri,null);
            Intent intent = new Intent();
            intent.setAction(CallRecordReceiver.CALL_RECORD_ACTION);
            getContext().sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        return number;
    }
}
