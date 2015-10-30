package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class Result {
	long id;
	double value;
	Calendar start_time;
	String session_key;
	User user;
	Test test;

	Result() {
		start_time = Calendar.getInstacne();
	}

	public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double newValue) {
		value = newValue;
	}

	public Calendar getStartTime() {
		return start_time;
	}

	public void setStartTiem(Calendar newStartTime) {
		start_time = newStartTime;
	}

	public String getSessionKey() {
		return session_key;
	}

	public void setSessionKey(String newSessionKey) {
		session_key = newSessionKey;
	}

	public User getUser() {
		return user;
	}

	public void setUser(newUser) {
		user = newUser;
	}

	public Test getTest() {
		return test;
	}

	public void setTest(newTest) {
		test = newTest;
	}

}