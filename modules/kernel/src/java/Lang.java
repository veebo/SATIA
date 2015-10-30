package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class Lang {
	long id;
	List<Phrase> phrases = new ArrayList<Phrase>();

	public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public List<Phrase> getPhrasesList() {
		return phrases;
	}

	public void addPhrase(Phrase newPhrase) {
		if ((newPhrase == null) || (phrases.contains(newPhrase))) {
			return;
		}
		newPhrase.setLang(this);
		phrases.add(newPhrase);
	}

	public void removePhrase(Phrase phraseToRm) {
		if ((phraseToRm == null) || (taskToRm.getLang() != this)) {
			return;
		}
		phrases.remove(phraseToRm);
		phraseToRm.setLang(null);
	}
}