package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "phrase")
public class Phrase {
    private long phraseId;
    private String value;
    private Lang lang;

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

    @OneToOne
    @JoinColumn(name = "lang_id", referencedColumnName = "lang_id", nullable = false)
    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
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


}
