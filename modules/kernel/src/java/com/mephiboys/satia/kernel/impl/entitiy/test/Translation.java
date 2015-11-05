package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
@Table(name = "translations", schema = "public", catalog = "quartz")
public class Translation {
    private long translationId;

    @Id
    @Column(name = "translation_id")
    public long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(long translationId) {
        this.translationId = translationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Translation that = (Translation) o;

        if (translationId != that.translationId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (translationId ^ (translationId >>> 32));
    }
}
