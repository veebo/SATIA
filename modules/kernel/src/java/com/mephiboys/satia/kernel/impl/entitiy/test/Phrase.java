package com.mephiboys.satia.kernel.impl.entitiy.test;

import javax.persistence.*;

/**
 * Created by vibo0315 on 05.11.2015.
 */
@Entity
public class Phrase {
    private long phraseId;
    private String value;
    private Translation translationsByTranslationId;

    @Id
    @Column(name = "phrase_id")
    public long getPhraseId() {
        return phraseId;
    }

    public void setPhraseId(long phraseId) {
        this.phraseId = phraseId;
    }

    @Basic
    @Column(name = "value")
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

        Phrase phrase = (Phrase) o;

        if (phraseId != phrase.phraseId) return false;
        if (value != null ? !value.equals(phrase.value) : phrase.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (phraseId ^ (phraseId >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "translation_id", referencedColumnName = "translation_id", nullable = false)
    public Translation getTranslationsByTranslationId() {
        return translationsByTranslationId;
    }

    public void setTranslationsByTranslationId(Translation translationsByTranslationId) {
        this.translationsByTranslationId = translationsByTranslationId;
    }
}
