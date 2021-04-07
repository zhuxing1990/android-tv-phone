package com.vunke.videochat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.manage.BackgroundManage;

/**
 * Created by zhuxi on 2020/11/7.
 */

public class BackgroundFragment extends Fragment implements View.OnKeyListener{
    private static final String TAG = "BackgroundFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private int[] imgArr = {R.mipmap.bg0,R.mipmap.bg1, R.mipmap.bg2, R.mipmap.bg3, R.mipmap.bg4,R.mipmap.bg5,R.mipmap.bg6};
    private ImageView bg_content, bg_img_up,bg_img_down;
    private RelativeLayout bg_relatvier1;
    //图片下标序号
    private int count = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_background,null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bg_relatvier1 = view.findViewById(R.id.bg_relatvier1);
        bg_relatvier1.requestFocus();
        bg_relatvier1.setOnKeyListener(this);
        bg_content = view.findViewById(R.id.bg_content);
        bg_img_up = view.findViewById(R.id.bg_img_up);
        bg_img_up.setVisibility(View.INVISIBLE);
        bg_img_down = view.findViewById(R.id.bg_img_down);
        bg_content.setImageResource(imgArr[count]);
        bg_relatvier1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (count){
                    case 0:
                        BackgroundManage.updateBackground(getActivity(),"0");
                        break;
                    case 1:
                        BackgroundManage.updateBackground(getActivity(),"1");
                        break;
                    case 2:
                        BackgroundManage.updateBackground(getActivity(),"2");
                        break;
                    case 3:
                        BackgroundManage.updateBackground(getActivity(),"3");
                        break;
                    case 4:
                        BackgroundManage.updateBackground(getActivity(),"4");
                        break;
                    case 5:
                        BackgroundManage.updateBackground(getActivity(),"5");
                        break;
                    case 6:
                        BackgroundManage.updateBackground(getActivity(),"6");
                        break;
                }
                View rootView = getActivity().findViewById(android.R.id.content);
                BackgroundManage.setBackground(getActivity(),rootView);
                Toast.makeText(getActivity(),"设置成功",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showRight() {
        if (bg_img_down.getVisibility()== View.INVISIBLE){
            bg_img_down.setVisibility(View.VISIBLE);
        }
    }

    private void hintLeft() {
        if (bg_img_up.getVisibility()== View.VISIBLE){
            bg_img_up.setVisibility(View.INVISIBLE);
        }
    }

    private void showLeft() {
        if (bg_img_up.getVisibility()== View.INVISIBLE){
            bg_img_up.setVisibility(View.VISIBLE);
        }
    }

    private void hintRight() {
        if (bg_img_down.getVisibility()== View.VISIBLE){
            bg_img_down.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (count < imgArr.length - 1) {
                    count++;
                    Log.i(TAG, "onKeyDown: RIGHT：" + count);
                    bg_content.setImageResource(imgArr[count]);
                    if (count == imgArr.length - 1) {
                        hintRight();
                    }
                    if (count > 0) {
                        showLeft();
                    }
                    return true;
                } else {
                    showLeft();
                    hintRight();
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (count > 0) {
                    count--;
                    Log.i(TAG, "onKeyDown: LEFT：" + count);
                    bg_content.setImageResource(imgArr[count]);
                    if (count == 0) {
                        hintLeft();
                    }
                    if (count < imgArr.length - 1) {
                        showRight();
                    }
                    return true;
                } else {
                    showRight();
                    hintLeft();
                }
            }
        }
        return false;
    }
}
