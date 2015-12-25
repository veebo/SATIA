package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "translation_id", referencedColumnName = "translation_id")
    private Translation translation;

    @Basic
    @Column(name = "source_num")
    private byte sourceNum;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "gen_id", referencedColumnName = "gen_id")
    private Generator generator;

    @ManyToMany(mappedBy = "tasks", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    private List<Test> tests;

    @OneToMany(mappedBy = "task")
    private List<FieldValue> fieldValues;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }


    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }


    public byte getSourceNum() {
        return sourceNum;
    }

    public void setSourceNum(byte sourceNum) {
        this.sourceNum = sourceNum;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }


    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<FieldValue> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(List<FieldValue> fieldValues) {
        this.fieldValues = fieldValues;
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
