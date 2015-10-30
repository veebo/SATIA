package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class User {
	long id;
	String name;
	String password;
	String email;
	List<Result> results = new ArrayList<Result>();
	List<Test> tests = new ArrayList<Test>();

	public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public String getName() {
		return name;
	}

	public void setName(long newName) {
		name = newName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(long newPassword) {
		password = newPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(long newEmail) {
		password = newEmail;
	}

	public List<Result> getResultsList() {
		return results;
	}

	public void addResult(Result newResult) {
		if ((newResult == null) || (results.contains(newResult))) {
			return;
		}
		newResult.setUser(this);
		results.add(newResult);
	}

	public void removeResult(Result resultToRm) {
		if ((resultToRm == null) || (resultToRm.getUser() != this)) {
			return;
		}
		results.remove(resultToRm);
		resultToRm.setUser(null);
	}

	public List<Test> getTestsList() {
		return tests;
	}

	public void addTest(Test newTest) {
		if ((newTest == null) || (tests.contains(newTest))) {
			return;
		}
		newTest.setUser(this);
		tests.add(newTest);
	}

	public void removeTest(Test testToRm) {
		if ((testToRm == null) || (testToRm.getUser() != this)) {
			return;
		}
		tests.remove(testToRm);
		testToRm.setUser(null);
	}

}