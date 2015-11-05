package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "generators")
public class Generator {
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

        Generator generator = (Generator) o;

        if (genId != generator.genId) return false;
        if (impl != null ? !impl.equals(generator.impl) : generator.impl != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (genId ^ (genId >>> 32));
        result = 31 * result + (impl != null ? impl.hashCode() : 0);
        return result;
    }
}
