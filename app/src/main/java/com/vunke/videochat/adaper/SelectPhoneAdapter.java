package com.vunke.videochat.adaper;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vunke.videochat.R;
import com.vunke.videochat.dialog.SelectPhoneDialog;
import com.vunke.videochat.manage.SelectPhoneManage;

import java.util.List;

/**
 * Created by zhuxi on 2020/9/14.
 */

public class SelectPhoneAdapter extends RecyclerView.Adapter<SelectPhoneAdapter.SelectPhoneHolder> {
    private static final String TAG = "SelectPhoneNumberAdapte";

    private List<String> list;
    private Activity context;
    public SelectPhoneAdapter(Activity context, List<String> list){
        this.context=context;
        this.list =list;
    }
    public void update(List<String> arrayList){
        list.clear();
        list.addAll(arrayList);
        notifyDataSetChanged();
    }
    @Override
    public SelectPhoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.recycler_selectphone,parent,false);
        SelectPhoneHolder holder = new SelectPhoneHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SelectPhoneHolder holder, int position) {
            final String phone_number = list.get(position);
            if (!TextUtils.isEmpty(phone_number)){
                holder.recycler_selectphone_text.setText(phone_number);
            }
            holder.recycler_selectphone_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                showDialog(phone_number);
                }
            });
    }
    private SelectPhoneDialog dialog;
    private void showDialog(final String phone_number) {
        if (dialog!=null&&dialog.isShow()){
            dialog.cancel();
        }
        dialog = new SelectPhoneDialog(context);
        dialog.setConfirmOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPhoneManage.fixedLineNumber(context,phone_number);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SelectPhoneHolder extends RecyclerView.ViewHolder{
        private View view ;
        private TextView recycler_selectphone_text;
        private RelativeLayout recycler_selectphone_rl;
        public SelectPhoneHolder(View itemView) {
            super(itemView);
            view = itemView;
            recycler_selectphone_text = view.findViewById(R.id.recycler_selectphone_text);
            recycler_selectphone_rl = view.findViewById(R.id.recycler_selectphone_rl);
        }
    }
}
