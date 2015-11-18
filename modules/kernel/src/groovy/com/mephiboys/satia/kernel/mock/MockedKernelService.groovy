package com.mephiboys.satia.kernel.mock
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*

import javax.persistence.EntityManager
import javax.sql.DataSource

import static org.mockito.Mockito.mock

class MockedKernelService implements KernelService {

    def tests = []
    def tasks = []
    def phrases = []
    def generators = []
    def users = []
    def results = []
    def langs = []

    def class2List = [
            (Test.class) : tests,
            (Task.class) : tasks,
            (Phrase.class) : phrases,
            (Generator.class) : generators,
            (User.class) : users,
            (Result.class) : results,
            (Lang.class) : langs

    ]

    @Override
    DataSource getDataSource() {
        return mock(DataSource.class)
    }


    @Override
    javax.persistence.EntityManager getEntityManager() {
        return mock(EntityManager.class)
    }

    @Override
    def <T> T getEntityById(Class<T> cls, Object id) {
        if (id == null) return null;
        print(cls)
        switch (cls) {
            case Test.class: tests.each { if(id.equals(((Test)it).getTestId())) return it}; break
            case Task.class: tasks.each { if(id.equals(((Task)it).getTaskId())) return it}; break
            case Phrase.class: phrases.each { if(id.equals(((Phrase)it).getPhraseId())) return it}; break
            case Generator.class: generators.each { if(id.equals(((Generator)it).getImpl())) return it}; break
            case User.class: users.each { if(id.equals(((User)it).getUsername())) return it}; break
            case Result.class: results.each { if(id.equals(((Result)it).getId())) return it}; break
            case Lang.class: langs.each { if(id.equals(((Lang)it).getLang())) return it}; break
        }

    }

    @Override
    def <T> Collection<T> getEntitiesByIds(Class<T> cls, Collection ids) {
        def results = []
        for (Object id : ids){
            if (id != null){
                def r = getEntityById(cls, id);
                if (r != null) results << r;
            }
        }
        return results
    }

    @Override
    def <T> T getEntityByQuery(Class<T> cls, String query, Object... params) {
        def list = class2List[cls]
        if (!list.isEmpty()) return list[0]
        return null
    }

    @Override
    def <T> Collection<T> getEntitiesByQuery(Class<T> cls, String query, Object... params) {
        def list = class2List[cls]
        if (!list.isEmpty()) return list
        return Collections.EMPTY_LIST
    }

    @Override
    void saveEntity(Object entity) {
        def cls = entity.getClass()
        switch (cls) {
            case Test.class: tests << ((Test)entity).setTestId(tests.max { ((Test)it).getTestId() } + 1); break
            case Task.class: tasks << ((Task)entity).setTaskId(tasks.max { ((Task)it).getTaskId() } + 1); break
            case Phrase.class: phrases << ((Phrase)entity).setPhraseId(phrases.max { ((Phrase)it).getPhraseId() } + 1); break
            case Generator.class: generators << ((Generator)entity).setGenId(generators.max { ((Generator)it).getGenId() } + 1); break
            case User.class: users << entity; break
            case Result.class: results << entity; break
            case Lang.class: langs << entity; break
        }
    }

    @Override
    void saveEntities(Collection entities) {
        for (Object o : entities){
            saveEntity(o);
        }
    }

    @Override
    def <T> void deleteEntityById(Class<T> cls, Object id) {
        if (id == null) return
        switch (cls) {
            case Test.class: tests.removeAll(id.equals(((Test)id).getTestId())); break
            case Task.class: tasks.removeAll(id.equals(((Task)id).getTaskId())); break
            case Phrase.class: phrases.removeAll(id.equals(((Phrase)id).getPhraseId())); break
            case Generator.class: generators.removeAll(id.equals(((Generator)id).getGenId())); break
            case User.class: users.removeAll(id.equals(((User)id).getUsername())); break
            case Result.class: results.removeAll(id.equals(((Result)id).getId())); break
            case Lang.class: langs.removeAll(id.equals(((Lang)id).getLang())); break
        }
    }

    @Override
    def <T> void deleteEntitiesByIds(Class<T> cls, Collection ids) {
        def results = []
        for (Object id : ids){
            if (id != null){
                deleteEntitiesByIds(cls, id);
            }
        }
    }

    @Override
    def <T> void deleteEntityByQuery(Class<T> cls, String query, Object... params) {
        //pass
    }

    @Override
    def <T> void deleteEntitiesByQuery(Class<T> cls, String query, Object... params) {
        //pass
    }

    {
        Lang eng = new Lang(lang:"eng")
        Lang rus = new Lang(lang:"rus")

        Phrase apple = new Phrase(phraseId: 1, value: "Apple", lang: eng)
        Phrase yabloko = new Phrase(phraseId: 2, value: "Яблоко", lang: rus)
        Phrase orange = new Phrase(phraseId: 3, value: "Orange", lang: eng)
        Phrase apelsin = new Phrase(phraseId: 4, value: "Апельсин", lang: rus)
        Phrase banana = new Phrase(phraseId: 5, value: "Banana", lang: eng)
        Phrase banan = new Phrase(phraseId: 6, value: "Банан", lang: rus)
        Phrase lemon = new Phrase(phraseId: 7, value: "Lemon", lang: eng)
        Phrase limon = new Phrase(phraseId: 8, value: "Лимон", lang: rus)

        Translation tr1 = new Translation(translationId: 1, phrase1: apple, phrase2: yabloko)
        Translation tr2 = new Translation(translationId: 2, phrase1: orange, phrase2: apelsin)
        Translation tr3 = new Translation(translationId: 3, phrase1: banana, phrase2: banan)
        Translation tr4 = new Translation(translationId: 4, phrase1: lemon, phrase2: limon)

        Task task1 = new Task(taskId: 1, translation: tr1, sourceNum: 2)
        Task task2 = new Task(taskId: 2, translation: tr2, sourceNum: 2)
        Task task3 = new Task(taskId: 3, translation: tr3, sourceNum: 2)
        Task task4 = new Task(taskId: 4, translation: tr4, sourceNum: 2)

        User u1 =  new User(
                username: "Guest",
                password: "HashedGuest",
                enabled: true
        )

        Generator g1 = new Generator(
                genId: 1,
                impl: "com.mephiboys.satia.generator.SimpleGenerator"
        )


        Test test1 = new Test(
                testId: 1,
                title: "Fruit test",
                description: "Try to pass the simplest test ever",
                createdWhen: new Date().toTimestamp(),
                user: u1,
                generator: g1,
                tasks: [task1, task2, task3, task4]
        )

        Result res1 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey1",
                                 value : 50.00, test : test1, user : null);
        Result res2 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey2",
                                 value : 60.00, test : test1, user : null);
        Result res3 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey3",
                                 value : 70.00, test : test1, user : null);

        tests << test1;
        tasks << task1 << task2 << task3 << task4;
        phrases << apple << orange << banana << lemon << yabloko << apelsin << banan << limon;
        langs << eng << rus;
        generators << g1;
        users << u1;
        results << res1 << res2 << res3;
    }

}