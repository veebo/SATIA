package com.mephiboys.satia.kernel.mock

import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*

import javax.persistence.EntityManager
import javax.sql.DataSource

import static org.mockito.Mockito.mock

import java.util.Calendar;

class MockedKernelService implements KernelService {

    def tests = []
    def tasks = []
    def phrases = []
    def generators = []
    def users = []
    def results = []

    @Override
    DataSource getDataSource() {
        return mock(DataSource.class)
    }


    @Override
    javax.persistence.EntityManager getEntityManager() {
        return mock(EntityManager.class)
    }

    @Override
    Collection<Test> getAllTests() {
        return tests;
    }

    @Override
    Test getTestById(long id) {
        for (Test t : tests) {
            if (t.getTestId() == id) {
                return t;
            }
        }
        return null;
    }

    @Override
    Collection<Test> getTestsById(long id) {
        return [getTestById(id)];
    }

    @Override
    Task getTaskById(long id) {
        for (Task t : tasks) {
            if (t.getTaskId() == id) {
                return t;
            }
        }
        return null;
    }

    @Override
    Collection<Task> getTasksById(long id) {
        return [getTaskById(id)];
    }

    @Override
    Object getByQuery(String query) {
        return null
    }

    @Override
    Phrase getPhraseById(long id) {
        for (Phrase p : phrases) {
            if (p.getPhraseId() == id) {
                return p;
            }
        }
        return null;
    }

    @Override
    Collection<Phrase> getPhrasesById(long id) {
        return [getPhraseById(id)];
    }

    @Override
    Generator getGeneratorById(long id) {
        for (Generator g : generators) {
            if (g.getGenId() == id) {
                return g;
            }
        }
        return null;
    }

    @Override
    Collection<Generator> getGeneratorsById(long id) {
        return [getGeneratorById(id)];
    }

    @Override
    Collection<Result> getResultsByTest(Test test) {
        def res = []
        results.each {
            if (it.getTest().equals(test)) {
                res << it;
            }
        }
        return res;
    }

    @Override
    Collection<Result> getResultsByTestId(long testId) {
        Test t = getTestById(testId);
        return getResultsByTest(t);
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

        Translation tr1 = new Translation(translationId: 1, phrase1Id: apple, phrase2Id: yabloko)
        Translation tr2 = new Translation(translationId: 2, phrase1Id: orange, phrase2Id: apelsin)
        Translation tr3 = new Translation(translationId: 3, phrase1Id: banana, phrase2Id: banan)
        Translation tr4 = new Translation(translationId: 4, phrase1Id: lemon, phrase2Id: limon)

        Task task1 = new Task(taskId: 1, translationId: 1, sourceNum: 2)
        Task task2 = new Task(taskId: 2, translationId: 2, sourceNum: 2)
        Task task3 = new Task(taskId: 3, translationId: 3, sourceNum: 2)
        Task task4 = new Task(taskId: 4, translationId: 4, sourceNum: 2)

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
                createdWhen: new Date(),
                user: u1,
                generator: g1,
                tasks: [task1, task2, task3, task4]
        )

        Result res1 = new Result(startTime : Calendar.getInstance(), sessionKey : "sessionKey1",
                                 value : 50.00, test : test1, user : null);
        Result res2 = new Result(startTime : Calendar.getInstance(), sessionKey : "sessionKey2",
                                 value : 60.00, test : test1, user : null);
        Result res3 = new Result(startTime : Calendar.getInstance(), sessionKey : "sessionKey3",
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
