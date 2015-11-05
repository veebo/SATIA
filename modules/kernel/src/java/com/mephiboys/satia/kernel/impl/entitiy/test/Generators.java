package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
public class Generators {
    private long genId;
    private String impl;

    @Id
    @Column(name = "gen_id")
    public long getGenId() {
        return genId;
    }

    public void setGenId(long genId) {
        this.genId = genId;
    }

    @Basic
    @Column(name = "impl")
    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Generators that = (Generators) o;

        if (genId != that.genId) return false;
        if (impl != null ? !impl.equals(that.impl) : that.impl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (genId ^ (genId >>> 32));
        result = 31 * result + (impl != null ? impl.hashCode() : 0);
        return result;
    }
}
