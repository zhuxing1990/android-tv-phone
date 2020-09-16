package com.vunke.videochat.dao

import android.content.ContentValues
import android.content.Context
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
//            var whereClause = "${ContactsTable.PHONE} = ?"
//            var whereArgs ={ "${ContactsTable.PHONE} = ${phone}" }
            contactsSQLite.use {
                count = delete(tableName = ContactsTable.TABLE_NAME, whereClause = whereClause)
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
}