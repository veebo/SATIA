package com.mephiboys.satia.groovy.controller

import com.mephiboys.satia.kernel.api.KernelService
import java.util.*;
import com.mephiboys.satia.kernel.impl.entitiy.*

public class EntityUpdater {
    KernelService ks;

    public EntityUpdater(KernelService ks) {
    	this.ks = ks;
    }

	def newPhrase(String newValue, Lang lang) {
        Object[] params = [newValue, lang.getLang()];
        Phrase equalPhrase = ks.getEntityByQuery(Phrase.class,"SELECT phrase_id FROM phrases "+
                                        "WHERE value=? AND lang_id=?", params);
        if (equalPhrase != null) {
            return equalPhrase;
        }
        else {
            Phrase new_phrase = new Phrase();
            new_phrase.setValue(newValue);
            new_phrase.setLang(lang);
            ks.saveEntity(new_phrase);
            return new_phrase;
        }
    }

    def newTask(String[] values, Generator gen, Test test) {
        Phrase p1 = newPhrase(values[0], test.getSourceLang());
        Phrase p2 = newPhrase(values[1], test.getTargetLang());
        Object[] params = [p1.getPhraseId(), p2.getPhraseId(), p2.getPhraseId(), p1.getPhraseId()];
        Translation tr = ks.getEntityByQuery(Translation.class,
  "SELECT translation_id FROM translations WHERE (phrase1_id=? AND phrase2_id=?) OR (phrase1_id=? AND phrase2_id=?)",
            params);
        if (tr == null) {
            tr = new Translation();
            tr.setPhrase1(p1);
            tr.setPhrase2(p2);
            ks.saveEntity(tr);
        }
        List<Test> tests = new ArrayList<Test>();
        tests.add(test);
        int sourceNum = (tr.getPhrase1().getLang().equals(test.getSourceLang())) ? 1 : 2;
        Task new_task = new Task();
        new_task.setTranslation(tr);
        new_task.setSourceNum(sourceNum);
        new_task.setGenerator(gen);
        new_task.setTests(tests);
        ks.saveEntity(new_task);
        return new_task;
    }

    def updatePhraseInTask(String newValue, int i, Task t, Test test) {
        if (newValue.equals("")) {
            return;
        }
        if (!newValue.equals(  t.getTranslation()."${"getPhrase"+i}"().getValue()  )) {
            //check if translation is used in other tasks
            Object[] params = [t.getTranslation().getTranslationId()];
            Collection<Task> relTasks = ks.getEntitiesByQuery(Task.class,
                                        "SELECT task_id FROM tasks where translation_id=?",
                                        params);
            //  if yes - create new translation
            if (!relTasks.isEmpty()) {
                Translation newTr = new Translation();
                newTr.setPhrase1(t.getTranslation().getPhrase1());
                newTr.setPhrase2(t.getTranslation().getPhrase2());
                ks.saveEntity(newTr);
                t.setTranslation(newTr);
                ks.saveEntity(t);
            }
            //check if this phrase is used in other translations
            Phrase phrase = t.getTranslation()."${"getPhrase"+i}"();
            params = [phrase.getPhraseId(), phrase.getPhraseId(), t.getTranslation().getTranslationId()];
            Collection<Translation> relTranslations = ks.getEntitiesByQuery(Translation.class,
            	            "SELECT translation_id FROM translations "+
                            "WHERE (phrase1_id=? OR phrase2_id=?) AND translation_id <> ?",
                                                        params);
            //  if not - replace old value
            if (relTranslations.isEmpty()) {
                phrase.setValue(newValue);
                ks.saveEntity(phrase);
            }
            //  if yes - create new phrase with value
            else {
                Phrase p = newPhrase(newValue, t.getTranslation()."${"getPhrase"+i}"().getLang());
                t.getTranslation()."${"setPhrase"+i}"(p);
                ks.saveEntity(t.getTranslation());
            }
        }
    }

    def removeTask(Task t, Test test) {
    	Translation tr = t.getTranslation();
    	Phrase p1 = tr.getPhrase1();
    	Phrase p2 = tr.getPhrase2();
        Object[] params = [tr.getTranslationId()];
    	Collection<Task> relTasks = ks.getEntitiesByQuery(Task.class,
    		"SELECT task_id FROM tasks WHERE translation_id=?", params);
    	if (relTasks().isEmpty()) {
    		ks.deleteEntityById(Translation.getClass(), tr.getTranslationId());
    	}
        params = [p1.getPhraseId(), p1.getPhraseId()];
    	Collection<Translation> relTranlations1 = ks.getEntitiesByQuery(Translation.class,
    		"SELECT translation_id FROM translations WHERE phrase1_id=? OR phrase2_id=?",
    		params);
    	if (relTranlations1.isEmpty()) {
    		ks.deleteEntityById(Phrase.getClass(), p1.getPhraseId());
    	}
        params = [p2.getPhraseId(), p2.getPhraseId()];
    	Collection<Translation> relTranlations2 = ks.getEntitiesByQuery(Translation.class,
    		"SELECT translation_id FROM translations WHERE phrase1_id=? OR phrase2_id=?",
    		params);
    	if (relTranlations2.isEmpty()) {
    		ks.deleteEntityById(Phrase.getClass(), p2.getPhraseId());
    	}
    	ks.deleteEntityById(Task.getClass(), t.getTaskId());
    }

}