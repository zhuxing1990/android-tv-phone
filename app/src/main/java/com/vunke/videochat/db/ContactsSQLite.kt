package com.vunke.videochat.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.*
import java.io.File

/**
 * Created by zhuxi on 2020/2/28.
 */
class ContactsSQLite(context :Context?):ManagedSQLiteOpenHelper(context!!, DATABASE_NAME,null, DATABASE_VERSION){
    var TAG = "ContactsSQLite"
    companion object {
        val DATABASE_NAME = "attn.db"
        val DATABASE_VERSION = 3
        private var instance : ContactsSQLite? = null
        @Synchronized
        fun getInstance(context: Context) : ContactsSQLite {
            if(instance == null){
                instance = ContactsSQLite(context.applicationContext)
            }
            return instance!!
        }
        var mainTmpDirSet = false
    }
    override fun getReadableDatabase(): SQLiteDatabase {
        if (!mainTmpDirSet) {
            File("/data/data/com.vunke.videochat/databases/main").mkdir()
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.vunke.videochat/databases/main'")
            mainTmpDirSet = true
            return super.getReadableDatabase()
        }
        return super.getReadableDatabase()
    }
    override fun onCreate(db: SQLiteDatabase?) {
        Log.i(TAG,"onCreate")
        try {
            db!!.createTable(
                    ContactsTable.TABLE_NAME,true,
                    ContactsTable._ID to INTEGER + PRIMARY_KEY ,
                    ContactsTable.USER_NAME to TEXT,
                    ContactsTable.PHONE to TEXT
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
        try {
            db!!.createTable(CallRecordTable.TABLE_NAME,true,
                    CallRecordTable._ID to INTEGER+PRIMARY_KEY+ AUTOINCREMENT,
                    CallRecordTable.CALL_NAME to TEXT,
                    CallRecordTable.CALL_PHONE to TEXT,
                    CallRecordTable.CALL_TIME to TEXT,
                    CallRecordTable.CALL_STATUS to TEXT
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i(TAG,"onUpgrade")
        try{
            db!!.dropTable(ContactsTable.TABLE_NAME, true)
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {
            db!!.dropTable(CallRecordTable.TABLE_NAME,true)
        }catch (e:Exception){
            e.printStackTrace()
        }
        try {
            onCreate(db)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}