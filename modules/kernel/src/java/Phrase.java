package com.mephiboys.satia.kernel.impl.entity;

import java.util.*;

public class Phrase {
	long id;
	String value;
	Lang lang;
	List<Phrase> translations = new ArrayList<Phrase>();

	public long getId() {
		return id;
	}

	public void setId(long newId) {
		id = newId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String newValue) {
		value = newValue;
	}

	public Lang getLang() {
		return lang;
	}

	public void setLang(Lang newLang) {
		lang = newLang;
	}

	public List<Phrase> getTranslationsList() {
		return translations;
	}

	public void addTranslation(Phrase newTranslation) {
		if ((newTranslation == null) || (translation.contains(newTranslation))) {
			return;
		}
		translations.add(newTranslation);
		newTranslation.getTranslationsList().add(this);
	}

	public void removeTranslation(Phrase translationToRm) {
		if (translationToRm == null) {
			return;
		}
		translations.remove(translationToRm);
		translationToRm.getTranslationsList().remove(this);
	}
	
}