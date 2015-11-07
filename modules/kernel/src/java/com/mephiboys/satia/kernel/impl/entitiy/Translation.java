package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "translations")
public class Translation {
    private long translationId;
    private long phrase1Id;
    private long phrase2Id;

    @Id
    @Column(name = "translation_id")
    public long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(long translationId) {
        this.translationId = translationId;
    }


    @Basic
    @Column(name = "phrase1_id")
    public long getPhrase1Id() {
        return phrase1Id;
    }

    public void setPhrase1Id(long phrase1Id) {
        this.phrase1Id = phrase1Id;
    }

    @Basic
    @Column(name = "phrase2_id")
    public long getPhrase2Id() {
        return phrase2Id;
    }

    public void setPhrase2Id(long phrase2Id) {
        this.phrase2Id = phrase2Id;
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
