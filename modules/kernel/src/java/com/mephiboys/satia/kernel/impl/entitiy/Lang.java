package com.mephiboys.satia.kernel.impl.entitiy;

import java.util.*;

public class Lang {
    protected long id;
    protected List<Phrase> phrases = new ArrayList<Phrase>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<Phrase> phrases) {
        this.phrases = phrases;
    }

}