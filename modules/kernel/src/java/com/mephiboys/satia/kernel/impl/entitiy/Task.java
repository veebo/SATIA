package com.mephiboys.satia.kernel.impl.entitiy;

import java.util.*;

public class Task {
    protected long id;
    protected List<Test> tests = new ArrayList<Test>();
    protected Translation translation;
    protected short source_num;
    protected Generator generator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }

    public short getSource_num() {
        return source_num;
    }

    public void setSource_num(short source_num) {
        this.source_num = source_num;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }
}