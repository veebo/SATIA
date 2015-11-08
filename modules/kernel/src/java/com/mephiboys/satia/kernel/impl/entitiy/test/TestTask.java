package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.*;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
@Table(name = "test_tasks", schema = "public", catalog = "quartz")
@IdClass(TestTaskPK.class)
public class TestTask {
    private long testId;
    private long taskId;
    private Test testsByTestId;

    @Id
    @Column(name = "test_id")
    public long getTestId() {
        return testId;
    }

    public void setTestId(long testId) {
        this.testId = testId;
    }

    @Id
    @Column(name = "task_id")
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

        TestTask testTask = (TestTask) o;

        if (testId != testTask.testId) return false;
        if (taskId != testTask.taskId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (testId ^ (testId >>> 32));
        result = 31 * result + (int) (taskId ^ (taskId >>> 32));
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "test_id", referencedColumnName = "test_id", nullable = false)
    public Test getTestsByTestId() {
        return testsByTestId;
    }

    public void setTestsByTestId(Test testsByTestId) {
        this.testsByTestId = testsByTestId;
    }
}
