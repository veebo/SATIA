package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "words")
public class Word {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long wordId;

    @Basic
    @Column(name = "value")
    private String value;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "part_of_speech_id", referencedColumnName = "id")
    private PartOfSpeech partOfSpeech;

    public Long getWordId() {
    	return wordId;
    }

    public void setWordId(Long wordId) {
    	this.wordId = wordId;
    }

    public String getValue() {
    	return value;
    }

    public void setValue(String value) {
    	this.value = value;
    }

    public PartOfSpeech getPartOfSpeech() {
    	return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
    	this.partOfSpeech = partOfSpeech;
    }

    @Override
    public boolean equals(Object o) {
    	if (this == o) return true;
    	
    	if ( (o == null) || (o.getClass() != getClass()) ) return false;

    	Word w = (Word)o;
    	if ((w.getWordId().equals(wordId)) && (w.getValue().equals(value)) && 
            (w.getPartOfSpeech().equals(partOfSpeech))) {
    		return true;
    	}

    	return false;
    }

}