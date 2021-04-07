package com.vunke.videochat.dao

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import com.vunke.videochat.base.BaseConfig
import com.vunke.videochat.db.Contacts
import com.vunke.videochat.db.ContactsSQLite
import com.vunke.videochat.db.ContactsTable
import com.vunke.videochat.model.ContactsList
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

/**
 * Created by zhuxi on 2020/2/28.
 */
class ContactsDao(mContext:Context){
    private var TAG = "ContactsDao"
    var contactsSQLite:ContactsSQLite
    companion object {
        private var instance : ContactsDao? = null
        @Synchronized
        fun getInstance(context: Context) : ContactsDao{
            if(instance == null){
                instance = ContactsDao(context)
            }
            return instance!!
        }
    }
    init {
        contactsSQLite =  ContactsSQLite.getInstance(mContext);
    }
    private fun converDomain2Map(data:Contacts):MutableMap<String, String>{
        var result = mutableMapOf<String, String>()
        try {
            //难点2
            with(data){
                //                result[MeterTitle._ID] = "$_id"
                result[ContactsTable.USER_NAME] = data.user_name
                result[ContactsTable.PHONE] = data.phone

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }
    private fun converDomain2Map(data:ContactsList.UserData.ContactData):MutableMap<String, String>{
        var result = mutableMapOf<String, String>()
        try {
            with(data){
                result[ContactsTable.USER_NAME] = data.friendsName
                result[ContactsTable.PHONE] = data.friendsNumber

            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return result
    }
    fun queryPhone(phone:String):List<Contacts>?{
        var contactsList:List<Contacts>? = null
        try {
            contactsSQLite.use {
                var Sqlwhere ="${ContactsTable.PHONE} = {phone}"
                var Sqlselect = select(ContactsTable.TABLE_NAME)
                        .where(Sqlwhere, ContactsTable.PHONE to phone)
                contactsList =  Sqlselect.parseList(object : MapRowParser<Contacts> {
                    override fun parseRow(columns: Map<String, Any?>): Contacts {
                        var contacts = Contacts()
                        contacts.user_name  = columns[ContactsTable.USER_NAME] as String
                        contacts.phone = columns[ContactsTable.PHONE] as String
                        contacts._id = columns[ContactsTable._ID] as Long
                        return contacts
                    }
                })
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return contactsList
    }
    fun queryName(user_name:String):List<Contacts>?{
        var contactsList:List<Contacts>? = null
        try {
            contactsSQLite.use {
                var Sqlwhere ="${ContactsTable.USER_NAME} = {user_name}"
                var Sqlselect = select(ContactsTable.TABLE_NAME)
                        .where(Sqlwhere, ContactsTable.USER_NAME to user_name)
                contactsList =  Sqlselect.parseList(object : MapRowParser<Contacts> {
                    override fun parseRow(columns: Map<String, Any?>): Contacts {
                        var contacts = Contacts()
                        contacts.user_name  = columns[ContactsTable.USER_NAME] as String
                        contacts.phone = columns[ContactsTable.PHONE] as String
                        contacts._id = columns[ContactsTable._ID] as Long
                        return contacts
                    }
                })
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return contactsList
    }
    fun queryAll():List<Contacts>?{
        var contactsList :List<Contacts>? =null
        try {
            contactsSQLite.use {
                var SQLselect = select(ContactsTable.TABLE_NAME)
                contactsList =  SQLselect.parseList(object:MapRowParser<Contacts>{
                    override fun parseRow(columns: Map<String, Any?>): Contacts {
                        var contacts = Contacts()
                        contacts.user_name  = columns[ContactsTable.USER_NAME] as String
                        contacts.phone = columns[ContactsTable.PHONE] as String
                        contacts._id = columns[ContactsTable._ID] as Long
                        return contacts
                    }
                })
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return contactsList
    }

    fun updateName(contacts: Contacts):Int{
        var count = -1
        try {
            contactsSQLite.use {
                var varargs = ContentValues()
                varargs.put(ContactsTable.USER_NAME,contacts.user_name)
                varargs.put(ContactsTable.PHONE,contacts.phone)
                var condition = "user_name=${contacts.user_name}"
                count = update(ContactsTable.TABLE_NAME,varargs,condition,null)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun updateName(contacts: ContactsList.UserData.ContactData)//:Int
    {
        var count = -1
        try {
            contactsSQLite.use {
                var varargs = ContentValues()
                varargs.put(ContactsTable.USER_NAME,contacts.friendsName)
                varargs.put(ContactsTable.PHONE,contacts.friendsNumber)
                var Sqlwhere ="${ContactsTable.USER_NAME} = {user_name}"
                var query = select(tableName = ContactsTable.TABLE_NAME )
                        .where(Sqlwhere, ContactsTable.USER_NAME to contacts.friendsName)
                var contactsList =query.parseList( object:MapRowParser<Contacts>{
                    override fun parseRow(columns: Map<String, Any?>): Contacts {
                        var contact = Contacts()
                        contact.user_name  = columns[ContactsTable.USER_NAME] as String
                        contact.phone = columns[ContactsTable.PHONE] as String
                        return contact
                    }
                })
                if (contactsList!=null&& contactsList.size!=0){
                    var condition = "${ContactsTable.USER_NAME}= ?"
                    var whereArgs = arrayOf("${contacts.friendsName}")
                    update(ContactsTable.TABLE_NAME,varargs,condition,whereArgs)
                }else{
                    saveData(contacts)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
//        back count
    }
    fun updatePhone(contacts: Contacts):Int{
        var count = -1
        try {
            contactsSQLite.use {
                var varargs = ContentValues()
                varargs.put(ContactsTable.USER_NAME,contacts.user_name)
                varargs.put(ContactsTable.PHONE,contacts.phone)
                var condition = "${ContactsTable.PHONE}= ?"
                var whereArgs = arrayOf("${contacts.phone}")
                count = update(ContactsTable.TABLE_NAME,varargs,condition,whereArgs)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun updateContacts(contacts: Contacts):Int{
        var count = -1
        try {
            contactsSQLite.use {
                var varargs = ContentValues()
                varargs.put(ContactsTable.USER_NAME,contacts.user_name)
                varargs.put(ContactsTable.PHONE,contacts.phone)
                var condition = "${ContactsTable._ID}= ?"
                var whereArgs = arrayOf("${contacts._id}")
                count = update(ContactsTable.TABLE_NAME,varargs,condition,whereArgs)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }

    fun saveData(contacts: Contacts):Long{
        var count = -1L
        try {
            contactsSQLite.use {
                val varargs = converDomain2Map(contacts).map {
                    Pair(it.key, it.value)
                }.toTypedArray()
                count = insert(ContactsTable.TABLE_NAME, *varargs)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun saveData(contacts: ContactsList.UserData.ContactData):Long{
        var count = -1L
        try {
            contactsSQLite.use {
                val varargs = converDomain2Map(contacts).map {
                    Pair(it.key, it.value)
                }.toTypedArray()
                count = insert(ContactsTable.TABLE_NAME, *varargs)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun saveDataList(contactsList: List<Contacts>){
        contactsSQLite.use {
            contactsList.forEach {
                var contacts = it
                var varargs = ContentValues()
                varargs.put(ContactsTable.USER_NAME,contacts.user_name)
                varargs.put(ContactsTable.PHONE,contacts.phone)
                var condition = "user_name=${contacts.user_name}"
                update(ContactsTable.TABLE_NAME,varargs,condition,null)
            }
        }
    }
    fun deleteUserName(user_name: String):Int{
        var count = 0
        try {
            var condition = "user_name=$user_name"
            contactsSQLite.use {
                count=  delete(ContactsTable.TABLE_NAME,condition,null)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun deletePhone(phone: String):Int{
        var count = -1
        try {
            var whereClause = "${ContactsTable.PHONE} = ${phone}"
            Log.i(TAG,"whereClause:$whereClause")
//            var whereClause = "${ContactsTable.PHONE} = ?"
//            var whereArgs ={ "${ContactsTable.PHONE} = ${phone}" }
            contactsSQLite.use {
//                count = delete(tableName = ContactsTable.TABLE_NAME, whereClause = whereClause)
                  count = delete(ContactsTable.TABLE_NAME,whereClause,ContactsTable.PHONE to phone)
//                count=  delete(ContactsTable.TABLE_NAME,whereClause,whereArgs)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }
    fun deleteAll():Int{
        var count = -1
        try {
            contactsSQLite.use {
                count=  delete(ContactsTable.TABLE_NAME)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count;
    }
    fun saveContacts(context: Context,contacts: Contacts){
        try {
            var resolver = context.contentResolver
            var contentValues = ContentValues()
            contentValues.put(ContactsTable.USER_NAME,contacts.user_name)
            contentValues.put(ContactsTable.PHONE,contacts.phone)
            contentValues.put(ContactsTable._ID,contacts._id)
            var uri = resolver.insert(Uri.parse(BaseConfig.CONTACTS_CONTENT_URL),contentValues)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun saveContacts(context: Context,contacts: ContactsList.UserData.ContactData):Int{
        var count = -1
        try {
            var resolver = context.contentResolver
            var contentValues = ContentValues()
            contentValues.put(ContactsTable.USER_NAME,contacts.friendsName)
            contentValues.put(ContactsTable.PHONE,contacts.friendsNumber)
            contentValues.put(ContactsTable._ID,contacts.id)
            var uri = resolver.insert(Uri.parse(BaseConfig.CONTACTS_CONTENT_URL),contentValues)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }

    fun deleteContacts(context: Context,contacts: Contacts):Int{
        var count =1
        try {
            var resolver = context.contentResolver
            val phoneList = arrayOf(contacts._id.toString())
            resolver.delete(Uri.parse(BaseConfig.CONTACTS_CONTENT_URL),"${ContactsTable._ID}=?",phoneList);
        }catch (e:Exception){
            e.printStackTrace()
        }
        return count
    }

    fun queryAll(context: Context):List<Contacts>{
        var contactsList :List<Contacts>? =null
        var resolver = context.contentResolver
        var cursor =resolver.query(Uri.parse(BaseConfig.CONTACTS_CONTENT_URL),null,null,null,null)
        if (cursor!=null){
            contactsList = ArrayList()
            while(cursor.moveToNext()){
                var contacts = Contacts();
                var phone = cursor.getString(cursor.getColumnIndex(ContactsTable.PHONE))
                var user_name = cursor.getString(cursor.getColumnIndex(ContactsTable.USER_NAME))
                var id = cursor.getLong(cursor.getColumnIndex(ContactsTable._ID))
                contacts._id = id
                contacts.user_name = user_name
                contacts.phone = phone
                contactsList.add(contacts)
            }
        }
        return contactsList!!
    }
}