package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "fields")
public class Field {

    @Id
    @Column(name = "field_id")
    private Long fieldId;

    @ManyToOne
    @JoinColumn(name = "gen_id", referencedColumnName = "gen_id")
    private Generator generator;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "type")
    private int type;

    @Basic
    @Column(name = "order")
    private int order;

    @Basic
    @Column(name = "multiple")
    private boolean multiple;

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
}