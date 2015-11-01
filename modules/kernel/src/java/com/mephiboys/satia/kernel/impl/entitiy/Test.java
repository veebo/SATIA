package com.mephiboys.satia.kernel.impl.entitiy;

import java.util.*;

public class Test {
    protected long id;
    protected String title;
    protected User user;
    protected String description;
    protected Generator generator;
    protected Calendar created_when;
    protected List<Result> results = new ArrayList<Result>();
    protected List<Task> tasks = new ArrayList<Task>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public Calendar getCreated_when() {
        return created_when;
    }

    public void setCreated_when(Calendar created_when) {
        this.created_when = created_when;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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
		if (resultToRm == null) {
			return;
		}
		results.remove(resultToRm);
		resultToRm.setTest(null);
	}

	public List<Task> getTasksList() {
		return tasks;
	}

	public void addTask(Task newTask) {
		if (newTask == null) {
			return;
		}
		tasks.add(newTask);
	}

	public void removeTask(Task taskToRm) {
		if (taskToRm == null) {
			return;
		}
		tasks.remove(taskToRm);
	}

}