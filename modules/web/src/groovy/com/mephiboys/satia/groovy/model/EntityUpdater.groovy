package com.mephiboys.satia.groovy.model

import java.util.*;
import javax.servlet.http.HttpSession

import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*

public class EntityUpdater {

    protected KernelService ks;

    public EntityUpdater(KernelService ks) {
    	this.ks = ks;
    }

    def updateTestFields(test, testReqParams) throws IllegalArgumentException {
            testReqParams.each {k,v ->
                if (k.equals("Generator")) {
                    long genId;
                    Generator newGen;
                    try {
                        genId = Long.parseLong(v);
                        newGen = ks.getEntityById(Generator.class, new Long(genId));
                    } catch (NumberFormatException nf) {
                        newGen = null;
                    }
                    if (newGen == null) {
                        throw new IllegalArgumentException("invalid generator: "+genId);
                    }
                    if (!test.getGenerator().equals(newGen)) {
                        test.setGenerator(newGen);
                    }
                }
                else if ((k.equals("SourceLang")) || (k.equals("TargetLang"))) {
                    Lang newLang = ks.getEntityById(Lang.class, v);
                    if (newLang == null) {
                        throw new IllegalArgumentException("ivalid "+k+": "+v);
                    }
                    if (!test."${"get"+k}"().equals(newLang)) {
                        test."${"set"+k}"(newLang);
                    }
                }
                else {
                    v = filterString(v)
                    if (!test."${"get"+k}"().equals(v)) {
                        test."${"set"+k}"(v);
                    }
                }
            };
            if (test.getSourceLang().equals(test.getTargetLang())) {
                throw new IllegalArgumentException("target and source languages are the same");
            }
            ks.saveEntity(test);
    }

	def newPhrase(String newValue, Lang lang, boolean filter) throws IllegalArgumentException {
        if (lang == null) {
            throw new IllegalArgumentException();
        }
        if (filter) {
            newValue = filterString(newValue);
        }

        Object[] params = [newValue, lang.getLang()];
        Phrase equalPhrase = ks.getEntityByQuery(Phrase.class, "SELECT phrase_id FROM phrases " +
                "WHERE value=? AND lang=?", params);
        if (equalPhrase != null) {
            return equalPhrase;
        } else {
            Phrase new_phrase = new Phrase(value : newValue, lang : lang);
            ks.saveEntity(new_phrase);
            return new_phrase;
        }
    }

    def newTask(String[] values, Generator gen, Test test)  throws IllegalArgumentException {
        ks.doInTransaction({
            if (test == null) {
                throw new IllegalArgumentException();
            }
            ks.mergeEntity(test)

            if (gen != null){
                ks.mergeEntity(gen)
            }
            if ((values == null) || (values.length < 2)) {
                throw new IllegalArgumentException("phrases not set");
            }

            Phrase p1 = newPhrase(values[0], test.getSourceLang(), true);
            Phrase p2 = newPhrase(values[1], test.getTargetLang(), true);

            Object[] params = [p1.getPhraseId(), p2.getPhraseId(), p2.getPhraseId(), p1.getPhraseId()];
            Translation tr = ks.getEntityByQuery(Translation.class,
                    "SELECT translation_id FROM translations WHERE (phrase1_id=? AND phrase2_id=?) OR (phrase1_id=? AND phrase2_id=?)",
                    params);
            if (tr == null) {
                tr = new Translation(phrase1: p1, phrase2: p2);
                ks.saveEntity(tr);
            }
            List<Test> tests = new ArrayList<Test>();
            tests.add(test);
            byte sourceNum = tr.getPhrase1().getLang().equals(test.getSourceLang()) ? (byte) 1 : (byte) 2;
            Task new_task = new Task(translation: tr, sourceNum: sourceNum, generator: gen, tests: tests);
            ks.saveEntity(new_task);
            return new_task;
        })
    }

    def updatePhraseInTask(String newValue, int i, Task t, Test test) throws IllegalArgumentException {
            if ((t == null) || (test == null) || (i < 1) || (i > 2)) {
                throw new IllegalArgumentException();
            }
            newValue = filterString(newValue);

            if (!newValue.equals(t.getTranslation()."${"getPhrase" + i}"().getValue())) {
                //check if translation is used in other tasks
                Object[] params = [t.getTranslation().getTranslationId(), t.getTaskId()];
                Collection<Task> relTasks = ks.getEntitiesByQuery(Task.class,
                        "SELECT task_id FROM tasks where translation_id = ? AND task_id <> ?",
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
                Phrase phrase = t.getTranslation()."${"getPhrase" + i}"();
                params = [phrase.getPhraseId(), phrase.getPhraseId(), t.getTranslation().getTranslationId()];
                Collection<Translation> relTranslations = ks.getEntitiesByQuery(Translation.class,
                        "SELECT translation_id FROM translations " +
                                "WHERE (phrase1_id = ? OR phrase2_id = ?) AND translation_id <> ?",
                        params);
                //  if not - replace old value
                if (relTranslations.isEmpty()) {
                    phrase.setValue(newValue);
                    ks.saveEntity(phrase);
                }
                //  if yes - create new phrase with value
                else {
                    Phrase p = newPhrase(newValue, t.getTranslation()."${"getPhrase" + i}"().getLang(), false);
                    t.getTranslation()."${"setPhrase" + i}"(p);
                    ks.saveEntity(t.getTranslation());
                }
            }
    }

    def removeTask(Task t, Test test) {
            if ((t == null) || (test == null)) {
                return;
            }
            Translation tr = t.getTranslation();
            Phrase p1 = tr.getPhrase1();
            Phrase p2 = tr.getPhrase2();

            ks.deleteEntityById(Task.class, t.getTaskId());

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
    }

    def updateTask(Test test, Task t, String[] values, Generator gen) throws IllegalArgumentException {
            if ((test == null) || (t == null)) {
                return;
            }
            //update phrases and translation
            for (int j = 1; ((values != null) && (j <= 2) && (values.length >= j)); j++) {
                updatePhraseInTask(values[j - 1], j, t, test);
            }
            //update sourceNum if needed
            if (!t.getTranslation()."${"getPhrase" + t.getSourceNum()}"().getLang().equals(test.getSourceLang())) {
                t.setSourceNum((t.getSourceNum() == (byte) 1) ? (byte) 2 : (byte) 1);
                ks.saveEntity(t);
            }
            //update generator
            if ((gen != null) && (!gen.equals(t.getGenerator()))) {
                t.setGenerator(gen);
                ks.saveEntity(t);
            }
    }

    /*def validateFieldValue(Field field, String value) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        value = filterString(value);
        switch (field.getType()) {
        case 1:
            try {
                Integer.parseInt(value, 10);
            }
            catch (NumberFormatException nf) {
                throw new IllegalArgumentException(field.getName() + " must be integer");
            }
            break;
        case 2:
            try {
                Double.parseDouble(value);
            }
            catch (NumberFormatException nf) {
                throw new IllegalArgumentException(field.getName() + " must be real number");
            }
            break;
        }
    }

    def updateTaskFieldValues(Task task, HttpServletRequest request, String paramPrefix) throws IllegalArgumentException {
        ks.doInTransaction({
            if ((task == null) || (request == null)) {
                return;
            }
            Generator gen = task.getGenerator();
            Collection<Field> genFields = ks.getEntitiesByQuery(Field.class,
                "SELECT field_id FROM fields WHERE gen_id = ?", gen.getGenId());
            def fieldValuesToSave = [];
            //validate values
            for (Field f : genFields) {
                String[] values = request.getParameterValues(paramPrefix + "_" + f.getName());
                if ((values == null) || (values.length < 1)) {
                    continue;
                }
                int addedValues = 0;
                for (String v : values) {
                    if ((!f.getMultiple()) || (addedValues > 0)) {
                        break;
                    }
                    validateFieldValue(f, v);
                    FieldValue newFieldValue = new FieldValue(field : f, task : task, value : v);
                    fieldValuesToSave << newFieldValue;
                    ++addedValues;
                }
            }
            //save values
            fieldValuesToSave.each { ks.saveEntity(it) }
        });
    }*/

    def saveResults(String username, Test test, HttpSession session, int rightAnswers) {
            ResultPK resPk = new ResultPK(username: username, testId: test.getTestId(),
                    startTime: new Date().toTimestamp(), sessionKey: session.getId());
            Result res = new Result(id: resPk, value: 100 * ((double)rightAnswers / test.getTasks().size()));
            ks.saveEntity(res);
            return res;
    }

    def filterString(String str) throws IllegalArgumentException {
            if (str == null) {
                throw new IllegalArgumentException("invalid string: " + str);
            }
            String filtered = str.replaceAll("[<>]{1}", "");
            if (filtered.equals("")) {
                throw new IllegalArgumentException("invalid string: " + str);
            }
            return filtered;
    }

}