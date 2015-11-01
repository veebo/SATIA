package com.mephiboys.satia.kernel.impl.entitiy;

import java.util.*;

public class Phrase {
    protected long id;
    protected String value;
    protected Lang lang;
    protected List<Phrase> translations = new ArrayList<Phrase>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<Phrase> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Phrase> translations) {
        this.translations = translations;
    }

    public void addTranslation(Phrase newTranslation) {
		if ((newTranslation == null) || (translations.contains(newTranslation))) {
			return;
		}
		translations.add(newTranslation);
	}

	public void removeTranslation(Phrase translationToRm) {
		if (translationToRm == null) {
			return;
		}
		translations.remove(translationToRm);
	}
	
}