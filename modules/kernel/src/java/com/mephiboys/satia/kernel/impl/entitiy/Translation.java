package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "translations")
public class Translation {
    private Long translationId;
    private Phrase phrase1;
    private Phrase phrase2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "translation_id")
    public Long getTranslationId() {
        return translationId;
    }

    public void setTranslationId(Long translationId) {
        this.translationId = translationId;
    }

    @ManyToOne
    @JoinColumn(name = "phrase1_id", referencedColumnName = "phrase_id")
    public Phrase getPhrase1() {
        return phrase1;
    }

    public void setPhrase1(Phrase phrase1) {
        this.phrase1 = phrase1;
    }

    @ManyToOne
    @JoinColumn(name = "phrase2_id", referencedColumnName = "phrase_id")
    public Phrase getPhrase2() {
        return phrase2;
    }

    public void setPhrase2(Phrase phrase2) {
        this.phrase2 = phrase2;
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
