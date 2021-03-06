package com.mephiboys.satia.kernel.impl;


import com.mephiboys.satia.kernel.api.AnswerGenerator;
import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.berkley.BerkeleyParserFactory;
import com.mephiboys.satia.kernel.impl.berkley.ParserHolder;
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
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;

@Singleton
@TransactionManagement(TransactionManagementType.CONTAINER)
public class KernelServiceEJB implements KernelService {

    private static final Logger log = org.apache.log4j.Logger.getLogger(KernelServiceEJB.class);

    private static final ParserHolder PARSER_HOLDER = initializeParserHolder();

    private static ParserHolder initializeParserHolder(){
        ParserHolder holder = ParserHolder.INSTANCE;
        holder.register("eng", BerkeleyParserFactory.create(new String[]{"-gr", "eng_sm6.gr"}));
        return holder;
    }

    private static final Map<String, AnswerGenerator> generators =
            new ConcurrentHashMap<String, AnswerGenerator>();

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
            if (e == null) {
                continue;
            }
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

    @Override
    public List<String> generateAnswers(String source, String translation, Task task) {
        Generator generator = task.getGenerator();
        if (generator == null){
            generator = task.getTests().get(0).getGenerator();
        }
        final String generatorClass = generator.getImpl();
        AnswerGenerator gen = generators.computeIfAbsent(generatorClass, genClass -> {
            try {
                return (AnswerGenerator)Class.forName(genClass).newInstance();
            } catch (Exception e) {
                new RuntimeException("Exception while class instantiation: " + genClass, e);
                return null;
            }
        });

        if (gen == null){
            throw new RuntimeException("Exception while class instantiation: " + generatorClass);
        }

        Object[] params = {task.getTaskId()};
        Collection<FieldValue> fieldValues = getEntitiesByQuery(FieldValue.class,
            "SELECT field_value_id FROM field_values WHERE task_id = ?", params);

        return gen.generate(source, translation, task, fieldValues);
    }

    @Override
    public ParserHolder getParserHolder() {
        return PARSER_HOLDER;
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
                        "SELECT task_id FROM tasks where translation_id = ?", params);
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
        if ( (test.getGenerator() != null) && (genId.equals(test.getGenerator().getGenId())) ) {
            return test.getGenerator();
        }
        for (Task t : test.getTasks()) {
            Generator gen = t.getGenerator();
            if (gen != null && genId.equals(gen.getGenId())) {
                return gen;
            }
        }
        return getEntityById(Generator.class, genId);
    }

    private Lang findLang(String langId, Test test) {
        if ( (test.getSourceLang() != null) && (test.getSourceLang().getLang().equals(langId)) ) {
            return test.getSourceLang();
        }
        if ( (test.getTargetLang() != null) && (test.getTargetLang().getLang().equals(langId)) ) {
            return test.getTargetLang();
        }
        return getEntityById(Lang.class, langId);
    }

//===============================================================================================
//==================================UPDATERS=====================================================
//===============================================================================================

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Test updateTest(Test test, boolean createTest, Map<String, String> testReqParams) {
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
                    newGen = findGenerator(genId, test);
                    if (newGen != null) {
                        test.setGenerator(newGen);
                    }
                } catch (NumberFormatException ignored) {}
            }
            else if ( (k.equals("SourceLang")) || (k.equals("TargetLang")) ) {
                Lang newLang = findLang(v, test);
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
                catch (Exception ignored) {}
            }
        }
        if ( (test.getGenerator() == null) || 
            (test.getSourceLang() == null) || (test.getTargetLang() == null) ||
            (test.getSourceLang().equals(test.getTargetLang())) ) {
            return null;
        }
        if (createTest) {
            saveEntity(test);
        } else {
            updateEntity(test);
        }
        return test;
    }

    public void removeTest(Test test) {
        if (test == null) {
            return;
        }
        //remove results
        Collection<Result> relResults = getEntitiesByQuery(Result.class,
            "SELECT username,test_id,start_time,session_key FROM results WHERE test_id=?", test.getTestId());
        for (Result r : relResults) {
            deleteEntityById(Result.class, r.getId());
        }
        //remove tasks
        removeTasks(new ArrayList<Task>(test.getTasks()), test);
        deleteEntityById(Test.class, test.getTestId());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private Phrase newPhrase(String newValue, Lang lang, Test test) {
        if (lang == null) {
            return null;
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
            if ((test == null) || (values == null) || (values.length < 2)) {
                return null;
            }
            String val1 = filterString(values[0]);
            String val2 = filterString(values[1]);

            if ((val1 == null) || (val2 == null)) {
                return null;
            }
            Phrase p1 = newPhrase(values[0], test.getSourceLang(), test);
            Phrase p2 = newPhrase(values[1], test.getTargetLang(), test);
            Generator gen = findGenerator(genId, test);
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
        for (Task t : tasks) {
            if (t != null) {
                test.getTasks().add(t);
            }
        }
        updateEntity(test);
    }

    private void updatePhraseInTask(String newValue, int i, Task task, Test test) {
            if ((task == null) || (test == null) || (i < 1) || (i > 2)) {
                return;
            }
            newValue = filterString(newValue);
            if (newValue == null) {
                return;
            }

            Phrase phraseToUpdate = ((i == 1) ? task.getTranslation().getPhrase1() : task.getTranslation().getPhrase2());
            Phrase otherPhrase = ((i == 2) ? task.getTranslation().getPhrase1() : task.getTranslation().getPhrase2());
            
            if (!newValue.equals(phraseToUpdate.getValue())) {

                Phrase existingPhrase = findEqualPhrase(newValue, phraseToUpdate.getLang(), test);
                Translation existingTrans = null;
                if (existingPhrase != null) {
                    existingTrans = findTranslationWithPhrases(otherPhrase, existingPhrase, test);
                }
                // if phrase with the same value exists
                //and translations with this phrase and other phrase in task exists
                //then replace translation, remove old translation if it's not related with any task
                //and remove old phrase if it's not related with any translation
                if (existingTrans != null) {
                    Translation oldTrans = task.getTranslation();
                    task.setTranslation(existingTrans);
                    updateEntity(task);
                    if (!existTasksWithTranslation(oldTrans, task, test)) {
                        deleteEntityById(Translation.class, oldTrans.getTranslationId());
                        if (!existTranslationsWithPhrase(phraseToUpdate, oldTrans, test)) {
                            deleteEntityById(Phrase.class, phraseToUpdate.getPhraseId());
                        }
                    }
                    return;
                }

                //if translation is used in other tasks - create new translation and set it to task
                if (existTasksWithTranslation(task.getTranslation(), task, test)) {
                    Translation newTr = new Translation();
                    newTr.setPhrase1(task.getTranslation().getPhrase1());
                    newTr.setPhrase2(task.getTranslation().getPhrase2());
                    saveEntity(newTr);
                    task.setTranslation(newTr);
                    updateEntity(task);
                }
                //if this phrase is not used in other translations
                if (!existTranslationsWithPhrase(phraseToUpdate, task.getTranslation(), test)) {
                    //if phrase with the same value doesn't exist - replace value
                    //else - replace old phrase with existing phrase and remove old phrase
                    if (existingPhrase == null) {
                        phraseToUpdate.setValue(newValue);
                        updateEntity(phraseToUpdate);
                    } else {
                        if (i == 1) {
                            task.getTranslation().setPhrase1(existingPhrase);
                        } else {
                            task.getTranslation().setPhrase2(existingPhrase);
                        }
                        updateEntity(task.getTranslation());
                        deleteEntityById(Phrase.class, phraseToUpdate.getPhraseId());
                    }
                }
                //if yes - create new phrase with new value or find existing phrase with the same value
                else {
                    Phrase newPhrase = (existingPhrase == null) ? newPhrase(newValue, phraseToUpdate.getLang(), test) : existingPhrase;
                    if (i == 1) {
                        task.getTranslation().setPhrase1(newPhrase);
                    } else {
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
                if (values[j - 1] != null) {
                    updatePhraseInTask(values[j - 1], j, task, test);
                }
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
                    Generator newGen = findGenerator(genId, test);
                    if (newGen != null) {
                        task.setGenerator(newGen);
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
    public List<FieldValue> addFieldValues(Field field, Task task, String[] values) {
        List<FieldValue> savedValues = new ArrayList<FieldValue>();
        if ((field == null) || (task == null) || (values == null) || (values.length < 1)) {
            return savedValues;
        }
        int savedValuesCount = 0;
        for (String v : values) {
            if ((!field.isMultiple()) && (savedValuesCount > 0)) {
                break;
            }
            if (v == null) {
                continue;
            }
            String validValue = validateFieldValue(field, v);
            if (validValue == null) {
                continue;
            }
            FieldValue newFieldValue = new FieldValue();
            newFieldValue.setField(field);
            newFieldValue.setTask(task);
            newFieldValue.setValue(validValue);
            saveEntity(newFieldValue);
            savedValues.add(newFieldValue);
            ++savedValuesCount;
        }
        return savedValues;
    }

    @Override
    public void updateFieldValue(FieldValue fValue, String newValue) {
        if ((fValue == null) || (newValue == null)) {
            return;
        }
        String validValue = validateFieldValue(fValue.getField(), newValue);
        if (validValue == null) {
            return;
        }
        fValue.setValue(validValue);
        updateEntity(fValue);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Result saveResult(String fullname, String username, Long testId, String sessionId, int rightAnswers, Date startTime) {
            Test test = getEntityById(Test.class, testId);
            Result res = new Result();
            res.setTest(test);
            res.setStartTime(new Timestamp(startTime.getTime()));
            res.setSessionKey(sessionId);
            res.setFullname(fullname);
            res.setUser(getEntityById(User.class, username));
            if (test.getTasks().size() > 0) {
                res.setValue(new Double( 100 * ((double)rightAnswers / (double)test.getTasks().size()) ));
            } else {
                res.setValue(new Double(0.00));
            }
            saveEntity(res);
            return res;
    }


//===============================================================================================
//==============================VALIDATORS=======================================================
//===============================================================================================

    public String filterString(String str) {
        String filtered = str;
        if (str != null) {
            filtered = str.replaceAll("[<>]{1}", "");
            if (filtered.equals("")) {
                filtered = null;
            }
        }
        return filtered;
    }

    private String validateFieldValue(Field field, String value) {
        if (field == null) {
            return null;
        }
        switch (field.getType()) {
        case 0:
            value = filterString(value);
            break;
        case 1:
            try {
                Integer.parseInt(value, 10);
            }
            catch (NumberFormatException nf) {
                return null;
            }
            break;
        case 2:
            try {
                Double.parseDouble(value);
            }
            catch (NumberFormatException nf) {
                return null;
            }
            break;
        }
        return value;
    }

}
