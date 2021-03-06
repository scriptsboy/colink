package com.aispeech.aios.adapter.service;

import java.util.ArrayList;
import java.util.List;

import com.aispeech.aios.adapter.bean.Contact;
import com.aispeech.aios.adapter.bean.Contact.PhoneInfoEntity;
import com.aispeech.aios.client.AIOSContactDataNode;
import com.google.gson.Gson;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class PhoneBookService extends IntentService {

	public PhoneBookService() {
		this("PhoneBookService");
	}

	public PhoneBookService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Uri uri = arg0.getParcelableExtra("uri");
		Log.d("zzj", uri.toString());
		ArrayList<Contact> list = getContact(uri);
		Gson gson = new Gson();
		String json = gson.toJson(list);
		AIOSContactDataNode.getInstance().postData(json);

	}

	public ArrayList<Contact> getContact(Uri uri) {
		ArrayList<Contact> listMembers = new ArrayList<Contact>();
		Cursor cursor = null;
		try {

			// 这里是获取联系人表的电话里的信息 包括：名字，名字拼音，联系人id,电话号码；
			// 然后在根据"sort-key"排序
			if (ContactsContract.CommonDataKinds.Phone.CONTENT_URI.equals(uri)) {
				cursor = getContentResolver().query(uri,
						new String[] { "display_name", "data1" }, null, null,
						null);
				if (cursor.moveToFirst()) {
					do {
						Contact contact = new Contact();
						String contact_phone = cursor
								.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						String name = cursor.getString(0);
						contact.setName(name);
						PhoneInfoEntity cp = new PhoneInfoEntity();
						cp.setNumber(contact_phone);
						List<PhoneInfoEntity> cps = new ArrayList<PhoneInfoEntity>();
						cps.add(cp);
						contact.setPhoneInfo(cps);
						if (name != null)
							listMembers.add(contact);
					} while (cursor.moveToNext());
				}
			} else {
				cursor = getContentResolver().query(uri,
						new String[] { "name", "number" }, null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					do {
						Contact contact = new Contact();
						String name = cursor.getString(cursor
								.getColumnIndex("name"));
						String contact_phone = cursor.getString(cursor
								.getColumnIndex("number"));
						contact.setName(name);
						PhoneInfoEntity cp = new PhoneInfoEntity();
						cp.setNumber(contact_phone);
						List<PhoneInfoEntity> cps = new ArrayList<PhoneInfoEntity>();
						cps.add(cp);
						contact.setPhoneInfo(cps);
						if (name != null)
							listMembers.add(contact);
						if (name != null)
							listMembers.add(contact);
					} while (cursor.moveToNext());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return listMembers;
	}

}
