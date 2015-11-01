package com.mephiboys.satia.kernel.impl.entitiy;

import java.util.*;

public class Generator {
	protected long id;
    protected String impl;
    protected List<Task> tasks = new ArrayList<Task>();
    protected List<Test> tests = new ArrayList<Test>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
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