package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "field_values")
public class FieldValue {

    @Id
    @Column(name = "field_value_id")
    private Long fieldValueId;

    @ManyToOne
    @JoinColumn(name = "field_id", referencedColumnName = "field_id")
    private Field field;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    private Task task;

    @Basic
    @Column(name = "value")
    private String value;

    public Long getFieldValueId() {
        return fieldValueId;
    }

    public void setFieldValueId(Long fieldValueId) {
        this.fieldValueId = fieldValueId;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldValue fieldValue = (FieldValue) o;

        if (fieldValueId != fieldValue.fieldValueId) return false;
        if (field != null ? !field.equals(fieldValue.field) : fieldValue.field != null) return false;
        if (task != null ? !task.equals(fieldValue.task) : fieldValue.task != null) return false;
        if (value != null ? !value.equals(fieldValue.value) : fieldValue.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (fieldValueId ^ (fieldValueId >>> 32));
        return result;
    }
}
