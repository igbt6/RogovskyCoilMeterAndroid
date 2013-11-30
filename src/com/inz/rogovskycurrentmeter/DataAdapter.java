package com.inz.rogovskycurrentmeter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DataAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	private String[] mNames;
	private String[] mValues;
	private int mViewResourceId;
	public DataAdapter(Context context, int resource, String[] names , String[] data) {
		super(context, resource, names);
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		         mNames = names;
				mValues = data;
				mViewResourceId = resource;
				}
				@Override
				public int getCount() {
				return mNames.length;
				}
				@Override
				public String getItem(int position) {
				return mNames[position];
				}
				@Override
				public long getItemId(int position) {
				return 0;
				}
				
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					convertView = mInflater.inflate(mViewResourceId, null);
					TextView iv = (TextView)convertView.findViewById(R.id.option_icon);
					iv.setText(mNames[position]);
					TextView tv = (TextView)convertView.findViewById(R.id.option_text);
					tv.setText(mNames[position]);
					return convertView;
					}
	

}
