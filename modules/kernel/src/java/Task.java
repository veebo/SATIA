package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class Task {
	long id;
	List<Test> tests = new ArrayList<Test>();
	Translation translation;
	short source_num;
    Generator generator;

    public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public List<Test> getTestsList() {
		return tests;
	}

	public void addTest(Test newTest) {
		tests.add(newTest);
	}

	public void removeTest(Test testToRm) {
		tests.remove(testToRm);
	}

	public Translation getTranslation() {
		return translation;
	}

	public void setTranslation(Translation newTranslation) {
		translation = newTranslation;
	}

	public short getSourceNum() {
		return source_num;
	}

	public void setSourceNum(short newSourceNum) {
		source_num = newSourceNum;
	}

	public Generator getGenerator() {
		return generator;
	}

	public void setGenerator(Generator newGenerator) {
		generator = newGenerator;
	}
	
}