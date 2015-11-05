package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by vibo0315 on 05.11.2015.
 */
public class TestTaskPK implements Serializable {
    private long testId;
    private long taskId;

    @Column(name = "test_id")
    @Id
    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    @Column(name = "task_id")
    @Id
    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestTaskPK that = (TestTaskPK) o;

        if (testId != that.testId) return false;
        if (taskId != that.taskId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (testId ^ (testId >>> 32));
        result = 31 * result + (int) (taskId ^ (taskId >>> 32));
        return result;
    }
}
