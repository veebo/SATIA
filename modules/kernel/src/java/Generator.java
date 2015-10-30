package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class Generator {
	long id;
	String impl;
	List<Task> tasks = new ArrayList<Task>();
	List<Test> tests = new ArrayList<Test>();

	public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public String getImpl() {
		return impl;
	}

	public void setImpl(String newImpl) {
		impl = newImpl;
	}

	public List<Task> getTasksList() {
		return tasks;
	}

	public void addTask(Task newTask) {
		if ((newTask == null) || (tasks.contains(newTask))) {
			return;
		}
		newTask.setGenerator(this);
		tasks.add(newTask);
	}

	public void removeTask(Task taskToRm) {
		if ((taskToRm == null) || (taskToRm.getGenerator() != this)) {
			return;
		}
		tasks.remove(taskToRm);
		taskToRm.setGenerator(null);
	}

	public List<Test> getTestsList() {
		return tests;
	}

	public void addTest(Test newTest) {
		if ((newTest == null) || (tests.contains(newTest))) {
			return;
		}
		newTest.setGenerator(this);
		tests.add(newTest);
	}

	public void removeTest(Test testToRm) {
		if ((testToRm == null) || (testToRm.getGenerator() != this)) {
			return;
		}
		tests.remove(testToRm);
		testToRm.setGenerator(null);
	}

}