package com.mephiboys.satia.kernel.impl.entitiy;

import java.util.*;

public class User {
    protected long id;
    protected String name;
    protected String password;
    protected String email;
    protected List<Result> results = new ArrayList<Result>();
    protected List<Test> tests = new ArrayList<Test>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
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