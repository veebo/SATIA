package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.*;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
@Table(name = "tasks", schema = "public", catalog = "quartz")
public class Task {
    private long taskId;
    private long translationId;

    @Id
    @Column(name = "task_id")
    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    @Basic
    @Column(name = "translation_id")
    public long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(long translationId) {
        this.translationId = translationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (taskId != task.taskId) return false;
        if (translationId != task.translationId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (taskId ^ (taskId >>> 32));
        result = 31 * result + (int) (translationId ^ (translationId >>> 32));
        return result;
    }
}
