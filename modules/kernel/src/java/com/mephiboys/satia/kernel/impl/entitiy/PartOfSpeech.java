package com.mephiboys.satia.kernel.impl.entitiy;

import javax.persistence.*;

@Entity
@Table(name = "parts_of_speech")
public class PartOfSpeech {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    public String getId() {
    	return id;
    }

    public void setId(String id) {
    	this.id = id;
    }

    @Override
    public boolean equals(Object o) {
    	if (this == o) return true;
    	if ( (o == null) || (o.getClass() != getClass()) ) return false;

    	PartOfSpeech p = (PartOfSpeech)o;
    	return p.getId().equals(this.id);
    }
}