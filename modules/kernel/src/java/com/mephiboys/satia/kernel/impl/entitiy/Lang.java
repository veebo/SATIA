package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "langs")
public class Lang {
    @Id
    @Column(name = "lang")
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lang lang1 = (Lang) o;

        if (lang != null ? !lang.equals(lang1.lang) : lang1.lang != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return lang != null ? lang.hashCode() : 0;
    }
}
