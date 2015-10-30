package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class Test {
	long id;
	String title;
	User user;
	String description;
	Generator generator;
	Calendar created_when;
	List<Result> results = new ArrayList<Result>();
	List<Task> tasks = new ArrayList<Task>();

	Test() {
		created_when = Calendar.getInstance();
	}

	public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(long newTitle) {
		title = newTitle;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User newUser) {
		user = newUser;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(long newDescription) {
		description = newDescription;
	}

	public Calendar getCreateDate() {
		return created_when;
	}

	public void setCreateDate(Calendar newDate) {
		created_when = newDate;
	}

	public Generator getGenerator() {
		return generator;
	}

	public void setGenerator(Generator newGenerator) {
		generator = newGenerator;
	}

	public List<Result> getResultsList() {
		return results;
	}

	public void addResult(Result newResult) {
		if ((newResult == null) || (results.contains(newResult))) {
			return;
		}
		newResult.setTest(this);
		results.add(newResult);
	}

	public void removeResult(Result resultToRm) {
		if ((resultToRm == null) || (resultsToRm.getTest() != this)) {
			return;
		}
		results.remove(resultToRm);
		resultToRm.setTest(null);
	}

	public List<Task> getTasksList() {
		return tasks;
	}

	public void addTask(Task newTask) {
		if ((newTask == null) || (tasks.contains(newTask))) {
			return;
		}
		tasks.add(newTask);
		newTask.addTest(this);
	}

	public void removeTask(Task taskToRm) {
		if (taskToRm == null) {
			return;
		}
		tasks.remove(taskToRm);
		taskToRm.removeTest(this);
	}

}