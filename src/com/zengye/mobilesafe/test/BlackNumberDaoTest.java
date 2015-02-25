package com.zengye.mobilesafe.test;

import java.util.List;
import java.util.Random;

import com.zengye.mobilesafe.db.dao.BlackNumberDBDao;
import com.zengye.mobilesafe.domain.BlackNumberInfo;

import android.test.AndroidTestCase;
import android.util.Log;

public class BlackNumberDaoTest extends AndroidTestCase {

	public void add() {
		BlackNumberDBDao dao = new BlackNumberDBDao(getContext());
		long l = 13129591000l;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			l += i;
			dao.add(l + "", (random.nextInt(3) + 1) +"");
		}
		dao.add("13078428220", "0");
	}
	
	public void update() {
		BlackNumberDBDao dao = new BlackNumberDBDao(getContext());
		dao.update("13078428220", "2");
	}
	
	public void find() {
		BlackNumberDBDao dao = new BlackNumberDBDao(getContext());
		
		assertEquals(true, dao.find("13078428220"));
	}
	
	public void findall() {
		// TODO Auto-generated method stub
		BlackNumberDBDao dao = new BlackNumberDBDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : infos) {
			Log.i("---->",blackNumberInfo.getNubmer());
			
		}
	}
	
	public void delete() {
		// TODO Auto-generated method stub
		BlackNumberDBDao dao = new BlackNumberDBDao(getContext());
		dao.delete("13078428220");
	}
}
