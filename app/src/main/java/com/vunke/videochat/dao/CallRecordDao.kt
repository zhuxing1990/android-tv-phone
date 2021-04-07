package com.vunke.videochat.dao

import android.content.ContentValues
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.vunke.videochat.db.CallRecord
import com.vunke.videochat.db.CallRecordTable
import com.vunke.videochat.db.ContactsSQLite
import com.vunke.videochat.db.ContactsTable
import org.jetbrains.anko.db.*

/**
 * Created by zhuxi on 2020/3/31.
 */
class CallRecordDao(context: Context) {
    private var TAG = "CallRecordDao"
//    var callRecortSQLite: CallRecordSQLite
    var contactsSQLite:ContactsSQLite
    companion object {
        private var  instance:CallRecordDao?=null
        fun getInstance(context:Context):CallRecordDao{
            if (null==instance){
                instance = CallRecordDao(context)
            }
            return instance!!
        }

    }
    init {
//        callRecortSQLite = CallRecordSQLite(context)
        contactsSQLite = ContactsSQLite(context)
    }

     fun insertData(callRecord: CallRecord):Long{
            var count = -1L
            try {
                contactsSQLite.use {
//                callRecortSQLite.use {
                    val varargs = converDomain2Map(callRecord).map {
                        Pair(it.key, it.value)
                    }.toTypedArray()
                    count = insert(CallRecordTable.TABLE_NAME, *varargs)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        return count
    }
    fun updateData(callRecord: CallRecord): Int {
        var count = 0
        try {
            contactsSQLite.use {
//            callRecortSQLite.use {
                var contentValues = ContentValues()
                contentValues.put(CallRecordTable.CALL_NAME,callRecord.call_name)
                contentValues.put(CallRecordTable.CALL_PHONE,callRecord.call_phone)
                contentValues.put(CallRecordTable.CALL_TIME,callRecord.call_time)
                contentValues.put(CallRecordTable.CALL_STATUS,callRecord.call_status)
                var condition = "call_phone=${callRecord.call_phone} and call_name=${callRecord.call_name}"
                count = update(CallRecordTable.TABLE_NAME,contentValues,condition,null)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun queryAll():List<CallRecord>?{
        var callRecordList :List<CallRecord>? =null
        try {
            contactsSQLite.use {
//            callRecortSQLite.use {
                var SQLselect = select(CallRecordTable.TABLE_NAME).orderBy("${CallRecordTable.CALL_TIME}", SqlOrderDirection.DESC);
                callRecordList =  SQLselect.parseList(object: MapRowParser<CallRecord> {
                    override fun parseRow(columns: Map<String, Any?>): CallRecord {
                        var callRecord = CallRecord()
                        callRecord.call_name  = columns[CallRecordTable.CALL_NAME] as String
                        callRecord.call_phone = columns[CallRecordTable.CALL_PHONE] as String
                        callRecord.call_time = columns[CallRecordTable.CALL_TIME] as String
                        callRecord.call_status = columns[CallRecordTable.CALL_STATUS] as String
                        return callRecord
                    }
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return callRecordList
    }
    fun queryAll2():List<CallRecord>?{
//        var callRecordList:List<CallRecord>?= mutableListOf<CallRecord>()
        var callRecordList=mutableListOf<CallRecord>()
        try {
            var db =contactsSQLite.writableDatabase
            var select = "select t1.call_name,t1.call_phone,t2.user_name from call_record as t1 left join attn as t2 on t1.call_phone = t2.phone"
            var select2 = "SELECT t1.${CallRecordTable.CALL_NAME},t1.${CallRecordTable.CALL_PHONE},t1.${ContactsTable.USER_NAME} FROM ${CallRecordTable.TABLE_NAME} as t1 LEFT JOIN ${ContactsTable.TABLE_NAME} as t2 ON t1.${CallRecordTable.CALL_PHONE} = t2.${ContactsTable.PHONE}"
            var c = db.rawQuery(select,null)
//            var table = "call_record as t1 left join attn as t2"
//            var columns= arrayOf("t1.call_name,t1.call_phone,t2.user_name")
//            var selection = ""
//            var selectionArgs = arrayOf("t2 on t1.call_phone = t2.phone")
//            var c =db.query( table, columns,  selection,selectionArgs, null, null,
//                    "${CallRecordTable.CALL_TIME} ${SqlOrderDirection.DESC}", null)
           if(c!=null){
               while (c.moveToNext()){
                   var callRecord = CallRecord()

                   callRecord.call_phone  = c.getString(c.getColumnIndex(CallRecordTable.CALL_PHONE))
                   callRecord.call_time   = c.getString(c.getColumnIndex(CallRecordTable.CALL_TIME))
                   callRecord.call_status  = c.getString(c.getColumnIndex(CallRecordTable.CALL_STATUS))
//                callRecord.call_name  = c.getString(c.getColumnIndex(CallRecordTable.CALL_NAME))
                   var call_name = c.getString(c.getColumnIndex(CallRecordTable.CALL_NAME))
                   var call_name2 = c.getString(c.getColumnIndex(ContactsTable.USER_NAME));
                   if (TextUtils.isEmpty(call_name2)){
                       callRecord.call_name = call_name
                   }else{
                       callRecord.call_name = call_name2
                   }
                   callRecordList.add(callRecord)
                   Log.i(TAG,"add")
               }
           }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return callRecordList
    }
    fun deleteData(callRecord: CallRecord):Int{
        var count = -1
        try {
            var condition = "${CallRecordTable.CALL_TIME} = ${callRecord.call_time} and ${CallRecordTable.CALL_PHONE} = ${callRecord.call_phone}"
            Log.i(TAG,"condition:$condition")
            contactsSQLite.use {
//            callRecortSQLite.use {
                count = delete(CallRecordTable.TABLE_NAME,condition,null);
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    private fun converDomain2Map(data: CallRecord):MutableMap<String, String>{
        var result = mutableMapOf<String, String>()
        try {
            with(data){
                //                result[MeterTitle._ID] = "$_id"
                result[CallRecordTable.CALL_PHONE] = data.call_phone
                result[CallRecordTable.CALL_NAME] = data.call_name
                result[CallRecordTable.CALL_TIME] = data.call_time
                result[CallRecordTable.CALL_STATUS] = data.call_status

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }
    fun deleteAll():Int{
        var count = -1
        try {
            contactsSQLite.use {
//            callRecortSQLite.use {
                count = delete(CallRecordTable.TABLE_NAME)
                Log.i(TAG,"delete count:$count")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }

}