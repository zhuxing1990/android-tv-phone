package com.vunke.videochat.adaper;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.dao.ContactsDao;
import com.vunke.videochat.db.Contacts;
import com.vunke.videochat.dialog.AddContactsDialog;
import com.vunke.videochat.dialog.NotCameraDialog;
import com.vunke.videochat.dialog.SetContactDialog;
import com.vunke.videochat.manage.CallManage;
import com.vunke.videochat.receiver.CallRecordReceiver;

import java.util.List;

/**
 * Created by zhuxi on 2020/2/28.
 */

public class ContactsAdaper extends RecyclerView.Adapter<ContactsAdaper.ContactsHolder> {
    private static final String TAG = "ContactsAdaper";
    private List<Contacts> list;
    private Activity context;
    private SetContactDialog setContactDialog;
    public ContactsAdaper(Activity context, List<Contacts> list){
        this.context = context;
        this.list = list;
    }
    public void update(List<Contacts> newList) {
        list.clear();
        list.addAll(newList);
        this.notifyDataSetChanged();
    }
    @Override
    public ContactsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_contacts,parent,false);
        ContactsHolder holder = new ContactsHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ContactsHolder holder,final int position) {
        holder.recycler_attn_name.setText(list.get(position).getUser_name());
        holder.recycler_attn_phone.setText(list.get(position).getPhone());
        holder.relative_attn_ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContanct(context,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ContactsHolder extends RecyclerView.ViewHolder{
        private ImageView recycler_attn_img;
        private TextView recycler_attn_name;
        private TextView recycler_attn_phone;
        private RelativeLayout relative_attn_ct;
        public ContactsHolder(View itemView) {
            super(itemView);
            relative_attn_ct = itemView.findViewById(R.id.relative_attn_ct);
            recycler_attn_img = itemView.findViewById(R.id.recycler_attn_img);
            recycler_attn_name = itemView.findViewById(R.id.recycler_attn_name);
            recycler_attn_phone = itemView.findViewById(R.id.recycler_attn_phone);
        }
    }
    private void setContanct(final Activity mcontext,final int position) {
        if (setContactDialog!=null&&setContactDialog.isShowing()){
            setContactDialog.cancel();
        }
        setContactDialog = new SetContactDialog(context).builder();
        setContactDialog.setCallAudioOnClickLinstener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfCameras = Camera.getNumberOfCameras();
                Log.i(TAG,"get camera number:"+numberOfCameras);
                if (numberOfCameras==0){
                    setContactDialog.cancel();
//                    new NotCameraDialog(context).Builder().show();
                    new NotCameraDialog(context).Builder(context).show();
                }else{
                    String phone = list.get(position).getPhone();
                    CallManage.CallAudio(mcontext,phone);
                    setContactDialog.cancel();
                }
            }
        });
        setContactDialog.setCallVideoOnClickLinstener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfCameras = Camera.getNumberOfCameras();
                Log.i(TAG,"get camera number:"+numberOfCameras);
                if (numberOfCameras==0){
                    setContactDialog.cancel();
                    new NotCameraDialog(context).Builder(context).show();
                }else{
                    String phone = list.get(position).getPhone();
                    CallManage.CallVideo(context,phone);
                    setContactDialog.cancel();
                }
            }
        });
        setContactDialog.setDelOnClickLinstener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = list.get(position).getPhone();
                int deletePhone = ContactsDao.Companion.getInstance(context).deletePhone(phone);
                if (deletePhone!=-1){
                    Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                    setContactDialog.cancel();
                }else{
                    Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                }
             List<Contacts> contactsList = ContactsDao.Companion.getInstance(mcontext).queryAll();
             update(contactsList);
            }
        });
        setContactDialog.setBackOnClickLinstener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContactDialog.cancel();
            }
        });
        setContactDialog.setEditOnClickLinsterer(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContactDialog.cancel();
                String phone = list.get(position).getPhone();
                String userName = list.get(position).getUser_name();
                Long id = list.get(position).get_id();
                AddContact(phone,userName,id);

            }
        });
        setContactDialog.show();
    }

    private AddContactsDialog dialog;
    private void AddContact(final String oldphone,final String oldname,final Long id) {
        if (dialog!=null&&dialog.isShowing()){
            dialog.cancel();
        }
        dialog = new AddContactsDialog(context).builder();
        dialog.setPhoenEdit(oldphone);
//        dialog.setNameEdit(oldname);
        dialog.setPositiveButton("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = dialog.getNameEdit().toString();
                String phone = dialog.getPhoenEdit().toString();

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(context,"请输入姓名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(context,"请输入号码",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (oldname.equals(name)&&oldphone.equals(phone)){
                    dialog.cancel();
                }else{
                    Contacts contacts = new Contacts();
                    contacts.setUser_name(name);
                    contacts.setPhone(phone);
                    contacts.set_id(id);
                    int updateContacts = ContactsDao.Companion.getInstance(context).updateContacts(contacts);
                    Intent intent = new Intent();
                    intent.setAction(CallRecordReceiver.CALL_RECORD_ACTION);
                    context.sendBroadcast(intent);
                    Log.i(TAG, "onClick: updateContacts:"+updateContacts);
                    if (updateContacts!=-1){
                        Log.i(TAG, "onClick: update success");
                    }else{
                        Log.i(TAG, "onClick: update failed");
                    }
                    List<Contacts> contactsList = ContactsDao.Companion.getInstance(context).queryAll();
                    update(contactsList);
                    dialog.cancel();
                }

            }
        });
        dialog.setNeutralButton("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
