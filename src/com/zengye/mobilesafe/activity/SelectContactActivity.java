package com.zengye.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.zengye.mobilesafe.R;

public class SelectContactActivity extends Activity {
	private ListView contactList;
	private List<Map<String, String>> contactData;
	private static final int RESULT_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		contactList = (ListView) findViewById(R.id.lv_select_contact);
		contactData = getContactsData();
		contactList.setAdapter(new SimpleAdapter(this, contactData,
				R.layout.lv_contact_list_item,
				new String[] { "name", "phone" }, new int[] {
						R.id.contact_name, R.id.contact_phone }));
		
		contactList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = contactData.get(position).get("phone");
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(RESULT_CODE, data);
				finish();
			}
		});
	}

	private List<Map<String, String>> getContactsData() {
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		ContentResolver resolver = getContentResolver();
		List<Map<String, String>> contactsData = new ArrayList<Map<String, String>>();
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);

		while (cursor.moveToNext()) {
			String contactId = cursor.getString(cursor
					.getColumnIndex("contact_id"));
			if (contactId != null) {
				Cursor dataCursor = resolver.query(uriData, new String[] {},
						"contact_id = ?", new String[] { contactId }, null);
				Map<String, String> data = new HashMap<String, String>();
				while (dataCursor.moveToNext()) {

					String data1 = dataCursor.getString(dataCursor
							.getColumnIndex("data1"));
					String mimetype = dataCursor.getString(dataCursor
							.getColumnIndex("mimetype"));
					if ("vnd.android.cursor.item/name".equals(mimetype)) {
						data.put("name", data1);
					} else if ("vnd.android.cursor.item/phone_v2"
							.equals(mimetype)) {
						data.put("phone", data1);
					}
				}
				contactsData.add(data);
				dataCursor.close();
			}
		}
		cursor.close();
		return contactsData;
	}
}
