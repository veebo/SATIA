package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
@Table(name = "langs", schema = "public", catalog = "quartz")
public class Lang {
    private String lang;

    @Id
    @Column(name = "lang")
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
