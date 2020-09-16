package com.vunke.videochat.db


/**
 * Created by zhuxi on 2020/3/30.
 */
//class CallRecordSQLite(context: Context) :ManagedSQLiteOpenHelper(context!!, DATABASE_NAME,null, DATABASE_VERSION) {
//    var TAG = "CallRecordSQLite"
//    companion object {
//        var DATABASE_NAME = "call_record.db"
//        var DATABASE_VERSION = 1
//        var  instance: CallRecordSQLite?= null
//        fun getInstance(context: Context): CallRecordSQLite {
//            if (instance==null){
//                instance = CallRecordSQLite(context)
//            }
//            back instance!!
//        }
//        var mainTmpDirSet = false
//    }
//    override fun onCreate(db: SQLiteDatabase?) {
//        Log.i(TAG,"onCreate")
//        try {
//            db!!.createTable(CallRecordTable.TABLE_NAME,true,
//                            CallRecordTable._ID to INTEGER+PRIMARY_KEY+ AUTOINCREMENT,
//                                        CallRecordTable.CALL_NAME to TEXT,
//                                        CallRecordTable.CALL_PHONE to TEXT,
//                                        CallRecordTable.CALL_TIME to TEXT,
//                                        CallRecordTable.CALL_STATUS to TEXT
//                             )
//        }catch (e:Exception){
//            e.printStackTrace()
//        }
//    }
//
//    override fun getReadableDatabase(): SQLiteDatabase {
//        if (!mainTmpDirSet) {
//            File("/data/data/com.vunke.videochat/databases/main").mkdir()
//            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory='/data/data/com.vunke.videochat/databases/main'")
//            mainTmpDirSet = true
//            back super.getReadableDatabase()
//        }
//        back super.getReadableDatabase()
//    }
//    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//       try {
//           Log.i(TAG,"onUpgrade")
//           db!!.dropTable(CallRecordTable.TABLE_NAME,true)
//           onCreate(db)
//       }catch (e:Exception){
//           e.printStackTrace()
//       }
//    }
//
//}