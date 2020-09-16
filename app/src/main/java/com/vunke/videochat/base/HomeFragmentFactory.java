package com.vunke.videochat.base;

import android.app.Fragment;

import com.vunke.videochat.fragment.AttnFragment;
import com.vunke.videochat.fragment.CallFragment;
import com.vunke.videochat.fragment.ContactsFragment;

import java.util.HashMap;
import java.util.Map;

public class HomeFragmentFactory {
	public static Map<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();

	public static Fragment createFragment(int position) {
		Fragment fragment = null;
		fragment = mFragments.get(position);
		if (fragment == null) { //如果等于null，说明集合中没有，就须要重新创建
			if (position == 0) {
				fragment = new CallFragment();
			} else if (position == 1) {
				fragment = new ContactsFragment();
			} else if (position == 2) {
				fragment = new AttnFragment();
			}
		}
		if (fragment != null) {
			mFragments.put(position, fragment);
		}
		return fragment;
	}
}
