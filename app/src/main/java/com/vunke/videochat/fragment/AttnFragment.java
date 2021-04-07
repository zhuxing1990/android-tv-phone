package com.vunke.videochat.fragment;

import android.app.Fragment;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.vunke.videochat.R;
import com.vunke.videochat.adaper.AttnAdapter;
import com.vunke.videochat.adaper.RecyclerViewSpacesItemDecoration;
import com.vunke.videochat.callback.CallRecordCallBack;
import com.vunke.videochat.dao.CallRecordDao;
import com.vunke.videochat.dao.ContactsDao;
import com.vunke.videochat.db.CallRecord;
import com.vunke.videochat.db.Contacts;
import com.vunke.videochat.receiver.CallRecordReceiver;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by zhuxi on 2020/2/27.
 */

public class AttnFragment extends Fragment {
    private static final String TAG = "AttnFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    private RecyclerView attn_recycler;
    private AttnAdapter attnAdapter;
    private CallRecordReceiver callRecordReceiver;
    private RelativeLayout attn_rl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attn,null);
        initView(view);
        initData();
        initRecordReceiver();
        return view;
    }

    private void initRecordReceiver() {
        callRecordReceiver = new CallRecordReceiver(new CallRecordCallBack() {
            @Override
            public void onUpdate() {
                Log.i(TAG, "Attn onUpdate: ");
                initData();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CallRecordReceiver.CALL_RECORD_ACTION);
        getActivity().registerReceiver(callRecordReceiver,intentFilter);
    }

    private void initView(View view) {
        attn_recycler = view.findViewById(R.id.attn_recycler);
        attn_rl = view.findViewById(R.id.attn_rl);
        attn_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: clear attn");
                int i = CallRecordDao.Companion.getInstance(getActivity()).deleteAll();
                if(i!=-1){
                    if (attnAdapter!=null){
                        attnAdapter.clear();
                    }
                    hindReccycler();
                    attn_rl.requestFocus();
                    Log.i(TAG, "onClick: delete success");
                }else{
                    Log.i(TAG, "onClick: delete failed");
                }
            }
        });
        initLinearManager();
    }

    private void initLinearManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        attn_recycler.setLayoutManager(linearLayoutManager);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,10);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,10);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,0);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,0);//右间距
        attn_recycler.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }

    private void initData() {
        Log.i(TAG, "initData: ");
        List<CallRecord> callRecordList = CallRecordDao.Companion.getInstance(getActivity()).queryAll();
        if (callRecordList!=null&&callRecordList.size()!=0){
            initAttnData(callRecordList);
        }else{
            hindReccycler();
            attn_rl.requestFocus();
        }
    }
    private DisposableObserver<List<CallRecord>> disposableObserver;
    private void initAttnData(final List<CallRecord> callRecordList) {
        Log.i(TAG, "initAttnData: ");
//        final List<Contacts> contacts = ContactsDao.Companion.getInstance(getActivity()).queryAll();//------------------数据库更新
        final List<Contacts> contacts = ContactsDao.Companion.getInstance(getActivity()).queryAll(getActivity());;
        if (contacts!=null&&contacts.size()!=0){
            disposedObserver();
            disposableObserver = new DisposableObserver<List<CallRecord>>() {
                @Override
                public void onNext(List<CallRecord> callRecords) {
                    if (callRecords != null && callRecords.size() != 0) {
                        setAdapterData(callRecords);
                    }
                    onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(TAG, "onError: ");
                    dispose();
                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "onComplete: ");
                    dispose();
                }
            };
            Observable.create(new ObservableOnSubscribe<List<CallRecord>>() {
                @Override
                public void subscribe(ObservableEmitter<List<CallRecord>> emitter) throws Exception {
                    if (contacts!=null&&contacts.size()!=0){
                        for (Contacts contact:contacts) {
                            String call_phone = contact.getPhone();
    //                Log.i(TAG, "initAttnData: call_phone:"+call_phone);
                            for (CallRecord callRecord:callRecordList) {
                                String phone = callRecord.getCall_phone();
    //                    Log.i(TAG, "initAttnData: phone:"+phone);
                                if (phone.equals(call_phone)){
                                    Log.i(TAG, "initAttnData: get name success:"+contact.getUser_name());
                                    callRecord.setCall_name(contact.getUser_name());
                                }
                                callRecord.setCall_id(contact.get_id());
                            }
                        }
                        emitter.onNext(callRecordList);
                    }else{
                        emitter.onNext(callRecordList);
                    }
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(disposableObserver);
        }else{
            setAdapterData(callRecordList);
        }
    }

    private void disposedObserver() {
        if (disposableObserver!=null&&!disposableObserver.isDisposed()){
            disposableObserver.dispose();
            disposableObserver = null;
        }
    }

    private void setAdapterData(List<CallRecord> callRecordList) {
//        if (attnAdapter!=null){
//            attnAdapter.update(callRecordList);
//            showRecycler();
//        }else{
            attnAdapter = new AttnAdapter(getActivity(),callRecordList);
            attn_recycler.setAdapter(attnAdapter);
            showRecycler();
//        }
    }

    private void showRecycler() {
        if (attn_recycler.getVisibility() == View.INVISIBLE){
            attn_recycler.setVisibility(View.VISIBLE);
        }
    }
    private void hindReccycler(){
        if (attn_recycler.getVisibility() == View.VISIBLE){
            attn_recycler.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        try {
            if (null!=callRecordReceiver) {
                getActivity().unregisterReceiver(callRecordReceiver);
            }
            disposedObserver();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
