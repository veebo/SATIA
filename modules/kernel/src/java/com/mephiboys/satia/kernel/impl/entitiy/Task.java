package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    private Long taskId;
    private Translation translation;
    private byte sourceNum;
    private Generator generator;
    private List<Test> tests;

    @Id
    @Column(name = "task_id")
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    @ManyToOne
    @JoinColumn(name = "translation_id", referencedColumnName = "translation_id")
    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
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

    @ManyToMany(mappedBy = "tasks", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (taskId ^ (taskId >>> 32));
        return result;
    }
    

}
