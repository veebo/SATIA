package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "phrases")
public class Phrase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phrase_id")
    private Long phraseId;

    @Basic
    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "lang", referencedColumnName = "lang", nullable = false)
    private Lang lang;

    public Long getPhraseId() {
        return phraseId;
    }

    public void setPhraseId(Long phraseId) {
        this.phraseId = phraseId;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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
