package com.vunke.videochat.adaper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * ViewPage+Fragment设配器
 * */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;



	// 自己重载一个构造方法 ，把碎片集合和 碎片管理器加进去
	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
	}



	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}
}
