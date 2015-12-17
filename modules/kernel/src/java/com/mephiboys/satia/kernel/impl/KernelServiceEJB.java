package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.*;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.Date;
import java.util.Map;
import java.sql.Timestamp;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;

@Singleton
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KernelServiceEJB implements KernelService {

    private static Logger log = org.apache.log4j.Logger.getLogger(KernelServiceEJB.class);

    @PersistenceContext (unitName = "PostgresPU")
    private EntityManager entityManager;

    @Resource(lookup="java:jboss/datasources/PostgresDSource")
    private DataSource dataSource;

    private JdbcTemplate jdbc;

    @PostConstruct
    protected void init(){
        jdbc = new JdbcTemplate(dataSource);
    }

    public DataSource getDataSource(){
        return dataSource;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public <T> T getEntityById(Class<T> cls, Object id) {
        return entityManager.find(cls, id);
    }

    @Override
    public <T> Collection<T> getEntitiesByIds(Class<T> cls, Collection ids) {
        if (cls == null || ids == null || ids.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        String sqlQuery;

        if (Test.class.equals(cls)){
            sqlQuery = "select e from Test e" + (ids.isEmpty() ? "" : " where e.testId IN :keys");
        } else if (Task.class.equals(cls)){
            sqlQuery = "select e from Task e" + (ids.isEmpty() ? "" : " where e.taskId IN :keys");
        } else if (Field.class.equals(cls)){
            sqlQuery = "select e from Field e" + (ids.isEmpty() ? "" : " where e.fieldId IN :keys");
        } else if (FieldValue.class.equals(cls)){
            sqlQuery = "select e from FieldValue e" + (ids.isEmpty() ? "" : " where e.fieldValueId IN :keys");
        } else if (Translation.class.equals(cls)){
            sqlQuery = "select e from Translation e" + (ids.isEmpty() ? "" : " where e.translationId IN :keys");
        } else if (Generator.class.equals(cls)){
            sqlQuery = "select e from Generator e" + (ids.isEmpty() ? "" : " where e.genId IN :keys");
        }else if (Phrase.class.equals(cls)){
            sqlQuery = "select e from Phrase e" + (ids.isEmpty() ? "" : " where e.phraseId IN :keys");
        } else if (Role.class.equals(cls)){
            sqlQuery = "select e from Role e" + (ids.isEmpty() ? "" : " where e.roleId IN :keys");
        }  else if (Lang.class.equals(cls)){
            sqlQuery = "select e from Lang e" + (ids.isEmpty() ? "" : " where e.lang IN :keys");
        } else if (User.class.equals(cls)){
            sqlQuery = "select e from User e" + (ids.isEmpty() ? "" : " where e.username IN :keys");
        }else if (Result.class.equals(cls)){
            sqlQuery = "select e from Result e" + (ids.isEmpty() ? "" : " where e.id IN :keys");
        }   else {
            throw new IllegalArgumentException("Class '"+cls+"' is not an entity");
        }

        Query q = entityManager.createQuery(sqlQuery, cls);
        if (!ids.isEmpty())
            q.setParameter("keys", new ArrayList(ids));
        List<T> result = q.getResultList();
        return result;
    }

    @Override
    public <T> T getEntityByQuery(Class<T> cls, String query, Object... params) {
        Collection<T> result = getEntitiesByQuery(cls, query, params);
        return result.isEmpty() ? null : result.iterator().next();
    }

    @Override
    public <T> Collection<T> getEntitiesByQuery(Class<T> cls, String query, Object... params) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        Class pkClass = null;
        RowMapper rowMapper = null;
        if (Test.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("test_id"); };
        } else if (Task.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("task_id"); };
        } else if (Field.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("field_id"); };
        } else if (FieldValue.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("field_value_id"); };
        }  else if (Translation.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("translation_id"); };
        } else if (Generator.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("gen_id");
            };
        } else if (Phrase.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("phrase_id"); };
        } else if (Role.class.equals(cls)){
            pkClass = Long.class;
            rowMapper = (rs, rowNum) -> { return rs.getLong("role_id"); };
        }  else if (Lang.class.equals(cls)){
            pkClass = String.class;
            rowMapper = (rs, rowNum) -> { return rs.getString("lang"); };
        } else if (User.class.equals(cls)){
            pkClass = String.class;
            rowMapper = (rs, rowNum) -> { return rs.getString("username"); };
        }else if (Result.class.equals(cls)){
            rowMapper = (rs, rowNum) -> {
                ResultPK pk = new ResultPK();
                pk.setSessionKey(rs.getString("session_key"));
                pk.setStartTime(rs.getTimestamp("start_time"));
                pk.setTestId(rs.getLong("test_id"));
                return pk;
            };
        } else {
            throw new IllegalArgumentException("Class '"+cls+"' is not an entity");
        }

        List keys = rowMapper == null
                ? jdbc.queryForList(query, pkClass, params)
                : jdbc.query(query, rowMapper, params);
        return getEntitiesByIds(cls, keys);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateEntity(Object entity) {
        entityManager.merge(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateEntities(Collection entities) {
        for (Object o : entities){
            entityManager.merge(o);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveEntity(Object entity) {
        entityManager.persist(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveEntities(Collection entities) {
        for (Object e : entities){
            entityManager.persist(e);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void deleteEntityById(Class<T> cls, Object id) {
        Object entity = entityManager.find(cls, id);
        if (entity == null){
           throw new RuntimeException("Object not found of class '" + cls + "' with id=" + id);
        }
        entityManager.remove(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void deleteEntitiesByIds(Class<T> cls, Collection ids) {
        Collection entities = getEntitiesByIds(cls, ids);
        for (Object e : entities){
            entityManager.remove(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public <T> void deleteEntitiesByQuery(Class<T> cls, String query, Object... params) {
        Collection entities = getEntitiesByQuery(cls, query, params);
        for (Object e : entities){
            entityManager.remove(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T doInTransaction(Callable<T> action) throws Exception {
        return action.call();
    }

//===============================================================================================
//==========================SELECT HELPERS=======================================================
//===============================================================================================

    private Phrase findEqualPhrase(String value, Lang lang, Test test) {
        for (Task t : test.getTasks()) {
            Phrase p1 = t.getTranslation().getPhrase1();
            Phrase p2 = t.getTranslation().getPhrase2();
            if ((p1.getLang().equals(lang)) && (p1.getValue().equals(value))) {
                return p1;
            }
            if ((p2.getLang().equals(lang)) && (p2.getValue().equals(value))) {
                return p2;
            }
        }
        Object[] params = {value, lang.getLang()};
        Phrase equalPhrase = getEntityByQuery(Phrase.class, "SELECT phrase_id FROM phrases " +
                "WHERE value=? AND lang=?", params);
        return equalPhrase;
    }

    private Translation findTranslationWithPhrases(Phrase phrase1, Phrase phrase2, Test test) {
        for (Task t : test.getTasks()) {
            Translation tr = t.getTranslation();
            if ( ( (tr.getPhrase1().equals(phrase1)) && (tr.getPhrase2().equals(phrase2)) ) ||
                 ( (tr.getPhrase1().equals(phrase2)) && (tr.getPhrase2().equals(phrase1)) ) ) {
                return tr;
            }
        }
        Object[] params = {phrase1.getPhraseId(), phrase2.getPhraseId(), phrase2.getPhraseId(), phrase1.getPhraseId()};
        Translation translation = getEntityByQuery(Translation.class,
            "SELECT translation_id FROM translations WHERE (phrase1_id=? AND phrase2_id=?) OR (phrase1_id=? AND phrase2_id=?)",
        params);
        return translation;
    }

    private boolean existTasksWithTranslation(Translation trans, Task task, Test test) {
        for (Task t : test.getTasks()) {
            if ( (!t.equals(task)) && (t.getTranslation().equals(trans)) ) {
                return true;
            }
        }
        Collection<Task> relTasks = null;
        if (task != null) {
            Object[] params = { trans.getTranslationId(), task.getTaskId() };
            relTasks = getEntitiesByQuery(Task.class,
                        "SELECT task_id FROM tasks where translation_id = ? AND task_id <> ?",
                        params);
        } else {
            Object[] params = { trans.getTranslationId() };
            relTasks = getEntitiesByQuery(Task.class,
                        "SELECT task_id FROM tasks where translation_id = ?",params);
        }
        return (!relTasks.isEmpty());
    }

    private boolean existTranslationsWithPhrase(Phrase phrase, Translation translation, Test test) {
        for (Task t : test.getTasks()) {
            if ( (!t.getTranslation().equals(translation)) && 
                ( (t.getTranslation().getPhrase1().equals(phrase)) || 
                  (t.getTranslation().getPhrase2().equals(phrase))    ) ) {
                return true;
            }
        }
        Collection<Translation> relTranslations = null;
        if (translation != null) {
            Object[] params = new Object[] {phrase.getPhraseId(), phrase.getPhraseId(), translation.getTranslationId()};
            relTranslations = getEntitiesByQuery(Translation.class, "SELECT translation_id FROM translations " +
                    "WHERE (phrase1_id = ? OR phrase2_id = ?) AND translation_id <> ?",
            params);
        } else {
            Object[] params = new Object[] {phrase.getPhraseId(), phrase.getPhraseId()};
            relTranslations = getEntitiesByQuery(Translation.class, "SELECT translation_id FROM translations " +
                    "WHERE (phrase1_id = ? OR phrase2_id = ?)",
            params);
        }
        return (!relTranslations.isEmpty());
    }

    private Generator findGenerator(Long genId, Test test) {
        if (genId == null) {
            return null;
        }
        if (genId.equals(test.getGenerator().getGenId())) {
            return test.getGenerator();
        }
        for (Task t : test.getTasks()) {
            if (t.getGenerator().getGenId().equals(genId)) {
                return t.getGenerator();
            }
        }
        return getEntityById(Generator.class, genId);
    }

//===============================================================================================
//==================================UPDATERS=====================================================
//===============================================================================================

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateTest(Test test, boolean createTest, Map<String, String> testReqParams) throws IllegalArgumentException {
        for (Map.Entry<String, String> entry : testReqParams.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (v == null) {
                continue;
            }
            if (k.equals("Generator")) {
                long genId = 0;
                Generator newGen;
                try {
                    genId = Long.parseLong(v);
                    newGen = getEntityById(Generator.class, genId);
                    if (newGen != null) {
                        test.setGenerator(newGen);
                    }
                } catch (NumberFormatException ignored) {}
            }
            else if ( (k.equals("SourceLang")) || (k.equals("TargetLang")) ) {
                Lang newLang = getEntityById(Lang.class, v);
                if (newLang != null) {
                    if (k.equals("SourceLang")) {
                        test.setSourceLang(newLang);
                    } else {
                        test.setTargetLang(newLang);
                    }
                }
            }
            else {
                try {
                    v = filterString(v);
                    if (k.equals("Title")) {
                        test.setTitle(v);
                    } else {
                        test.setDescription(v);
                    }
                }
                catch (IllegalArgumentException ignored) { }
            }
        }
        if (test.getSourceLang().equals(test.getTargetLang())) {
            throw new IllegalArgumentException("target and source languages are the same");
        }
        if (createTest) {
            saveEntity(test);
        }
        else {
            updateEntity(test);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private Phrase newPhrase(String newValue, Lang lang, Test test, boolean filter) throws IllegalArgumentException {
        if (lang == null) {
            throw new IllegalArgumentException();
        }
        if (filter) {
            newValue = filterString(newValue);
        }

        Phrase equalPhrase = findEqualPhrase(newValue, lang, test);
        if (equalPhrase != null) {
            return equalPhrase;
        } else {
            Phrase newPhrase = new Phrase();
            newPhrase.setValue(newValue);
            newPhrase.setLang(lang);
            saveEntity(newPhrase);
            return newPhrase;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Task newTask(String[] values, Long genId, Test test) {
            if (test == null) {
                return null;
            }
            if ((values == null) || (values.length < 2)) {
                return null;
            }

            Generator gen = findGenerator(genId, test);
            Phrase p1;
            Phrase p2;
            try {
                p1 = newPhrase(values[0], test.getSourceLang(), test, true);
                p2 = newPhrase(values[1], test.getTargetLang(), test, true);
            } catch (Exception ie) {
                return null;
            }

            Translation tr = findTranslationWithPhrases(p1, p2, test);
            if (tr == null) {
                tr = new Translation();
                tr.setPhrase1(p1);
                tr.setPhrase2(p2);
                saveEntity(tr);
            }
            List<Test> tests = new ArrayList<Test>();
            tests.add(test);
            byte sourceNum = tr.getPhrase1().getLang().equals(test.getSourceLang()) ? (byte) 1 : (byte) 2;
            Task newTask = new Task();
            newTask.setTranslation(tr);
            newTask.setSourceNum(sourceNum);
            newTask.setGenerator(gen);
            newTask.setTests(tests);
            return newTask;
    }

    @Override
    public void createTasks(Test test, List<Task> tasks) {
        saveEntities(tasks);
        test.getTasks().addAll(tasks);
        updateEntity(test);
    }

    private void updatePhraseInTask(String newValue, int i, Task task, Test test) throws IllegalArgumentException {
            if ((task == null) || (test == null) || (i < 1) || (i > 2)) {
                throw new IllegalArgumentException();
            }
            newValue = filterString(newValue);

            Phrase phraseToUpdate = ((i == 1) ? task.getTranslation().getPhrase1() : task.getTranslation().getPhrase2());
            if (!newValue.equals(phraseToUpdate.getValue())) {
                //  if translation is used in other tasks - create new translation
                if (existTasksWithTranslation(task.getTranslation(), task, test)) {
                    Translation newTr = new Translation();
                    newTr.setPhrase1(task.getTranslation().getPhrase1());
                    newTr.setPhrase2(task.getTranslation().getPhrase2());
                    saveEntity(newTr);
                    task.setTranslation(newTr);
                    updateEntity(task);
                }
                //  if this phrase is not used in other translations - replace old value
                if (!existTranslationsWithPhrase(phraseToUpdate, task.getTranslation(), test)) {
                    phraseToUpdate.setValue(newValue);
                    updateEntity(phraseToUpdate);
                }
                //  if yes - create new phrase with new value
                else {
                    Phrase newPhrase = newPhrase(newValue, phraseToUpdate.getLang(), test, false);
                    if (i == 1) {
                        task.getTranslation().setPhrase1(newPhrase);
                    }
                    else {
                        task.getTranslation().setPhrase2(newPhrase);
                    }
                    updateEntity(task.getTranslation());
                }
            }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeTasks(List<Task> tasks, Test test) {
        if ((tasks == null) || (tasks.isEmpty()) || (test == null)) {
            return;
        }

        test.getTasks().removeAll(tasks);
        updateEntity(test);

        for (Task task : tasks){
            Translation tr = task.getTranslation();
            Phrase p1 = tr.getPhrase1();
            Phrase p2 = tr.getPhrase2();
            deleteEntityById(Task.class, task.getTaskId());
            if (!existTasksWithTranslation(tr, null, test)) {
                deleteEntityById(Translation.class, tr.getTranslationId());
                if (!existTranslationsWithPhrase(p1, null, test)) {
                    deleteEntityById(Phrase.class, p1.getPhraseId());
                }
                if (!existTranslationsWithPhrase(p2, null, test)) {
                    deleteEntityById(Phrase.class, p2.getPhraseId());
                }
            }
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateTask(Test test, Task task, String[] values, Long genId) {
            if ((test == null) || (task == null)) {
                return;
            }

            //update phrases and translation
            for (int j = 1; ( (values != null) && (j <= 2) && (values.length >= j) ); j++) {
                try {
                    updatePhraseInTask(values[j - 1], j, task, test);
                } catch (Exception ignored) { continue; }
            }
            boolean changed = false;
            //update sourceNum if needed
            Phrase sourcePhrase = ( (task.getSourceNum() == (byte)1) ?
                                       task.getTranslation().getPhrase1() :
                                       task.getTranslation().getPhrase2() );
            if (!sourcePhrase.getLang().equals(test.getSourceLang())) {
                task.setSourceNum( (task.getSourceNum() == (byte)1) ? (byte)2 : (byte)1 );
                changed = true;
            }
            //update generator
            if (genId != null) {
                if (genId.equals(new Long(-1))) {
                    task.setGenerator(null);
                    changed = true;
                } else {
                    Generator taskGen = getEntityById(Generator.class, genId);
                    if (taskGen != null) {
                        task.setGenerator(taskGen);
                        changed = true;
                    }
                }
            }
            //save task
            if (changed) {
                updateEntity(task);
            }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateTaskFieldValues(Task task, HttpServletRequest request, String paramPrefix) throws IllegalArgumentException {
        if ((task == null) || (request == null)) {
            throw new IllegalArgumentException();
        }

        Generator gen = task.getGenerator();
        Object[] params = {gen.getGenId()};
        Collection<Field> genFields = getEntitiesByQuery(Field.class,
                "SELECT field_id FROM fields WHERE gen_id = ?", params);
        List<FieldValue> fieldValuesToSave = new ArrayList<FieldValue>();
        //validate values
        for (Field f : genFields) {
            String[] values = request.getParameterValues(paramPrefix + "_" + f.getName());
            if ((values == null) || (values.length < 1)) {
                continue;
            }
            int addedValues = 0;
            for (String v : values) {
                if ((!f.isMultiple()) || (addedValues > 0)) {
                    break;
                }
                validateFieldValue(f, v);
                FieldValue newFieldValue = new FieldValue();
                newFieldValue.setField(f);
                newFieldValue.setTask(task);
                newFieldValue.setValue(v);
                fieldValuesToSave.add(newFieldValue);
                ++addedValues;
            }
        }
        //save values
        for (FieldValue fv : fieldValuesToSave) {
            saveEntity(fv);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Result saveResult(String fullname, String username, Long testId, String sessionId, int rightAnswers) {
            Test test = getEntityById(Test.class, testId);
            Result res = new Result();
            res.setTest(test);
            res.setStartTime(new Timestamp(new Date().getTime()));
            res.setSessionKey(sessionId);
            res.setFullname(fullname);
            res.setUser(getEntityById(User.class, username));
            res.setValue(new Double( 100 * ((double)rightAnswers / (double)test.getTasks().size()) ));
            saveEntity(res);
            return res;
    }

//===============================================================================================
//==============================VALIDATORS=======================================================
//===============================================================================================

    private String filterString(String str) throws IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException("invalid string: " + str);
        }
        String filtered = str.replaceAll("[<>]{1}", "");
        if (filtered.equals("")) {
            throw new IllegalArgumentException("invalid string: " + str);
        }
        return filtered;
    }

    private void validateFieldValue(Field field, String value) throws IllegalArgumentException {
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
                throw new IllegalArgumentException();
            }
            break;
        case 2:
            try {
                Double.parseDouble(value);
            }
            catch (NumberFormatException nf) {
                throw new IllegalArgumentException();
            }
            break;
        }
    }

}
