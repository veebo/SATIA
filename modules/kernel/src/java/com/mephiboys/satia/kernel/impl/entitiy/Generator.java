package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "generators")
public class Generator {


    private Long genId;

    @Basic
    @Column(name = "impl")
    private String impl;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gen_id")
    public Long getGenId() {
        return genId;
    }

    public void setGenId(Long genId) {
        this.genId = genId;
    }

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

        Generator that = (Generator) o;

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
