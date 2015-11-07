package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    private long taskId;
    private long translationId;
    private byte sourceNum;
    private Generator generator;
    private List<Test> tests;

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

    @Basic
    @Column(name = "source_num")
    public byte getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(byte sourceNum) {
        this.sourceNum = sourceNum;
    }

    @ManyToOne
    @JoinColumn(name = "gen_id", referencedColumnName = "gen_id")
    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    @ManyToMany(mappedBy = "tasks")
    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
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
