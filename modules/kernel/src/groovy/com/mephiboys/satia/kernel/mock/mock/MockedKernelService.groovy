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

    @Override
    DataSource getDataSource() {
        return mock(DataSource.class)
    }


    @Override
    javax.persistence.EntityManager getEntityManager() {
        return mock(EntityManager.class)
    }

    @Override
    Test getTestById(long id) {
        for (t : tests) {
            if (t.getTestId() == id) {
                return t;
            }
        }
    }

    @Override
    Collection<Test> getTestsById(long id) {
        return [getTestById(id)];
    }

    @Override
    Task getTaskById(long id) {
        for (t : tasks) {
            if (t.getTaskId() == id) {
                return t;
            }
        }
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
        for (p : phrases) {
            if (p.getPhraseId() == id) {
                return p;
            }
        }
    }

    @Override
    Collection<Phrase> getPhrasesById(long id) {
        return [getPhraseById(id)];
    }

    @Override
    Generator getGeneratorById(long id) {
        for (g : generators) {
            if (g.getGenId() == id) {
                return g;
            }
        }
    }

    @Override
    Collection<Generator> getGeneratorsById(long id) {
        return [getGeneratorById(id)];
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

        tests << test1;
        tasks << task1 << task2 << task3 << task4;
        phrases << apple << orange << banana << lemon << yabloko << apelsin << banan << limon;
        langs << eng << rus;
        generators << g1;
    }

}
