package com.mephiboys.satia.kernel.test
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*

class DbAction {

    def static fillDb(KernelService ks){

        def user = ks.getEntityById(User.class, "sadmin")

        ks.doInTransaction({
            def tests = []
            def tasks = []
            def translations = []
            def phrases = []
            def generators = []
            def results = []
            def langs = []

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

            Generator g1 = new Generator(
                    genId: 1,
                    impl: "com.mephiboys.satia.generator.SimpleGenerator"
            )


            Test test1 = new Test(
                    testId: 1,
                    title: "Fruit test",
                    description: "Try to pass the simplest test ever",
                    createdWhen: new Date().toTimestamp(),
                    user: user,
                    generator: g1,
                    tasks: [task1, task2, task3, task4],
                    sourceLang: rus,
                    targetLang: eng
            )

            Result res1 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey1",
                    value : 50.00, test : test1, user : null);
            Result res2 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey2",
                    value : 60.00, test : test1, user : user);
            Result res3 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey3",
                    value : 70.00, test : test1, user : user);

            tests << test1;
            tasks << task1 << task2 << task3 << task4;
            translations << tr1 << tr2 << tr3 << tr4;
            phrases << apple << orange << banana << lemon << yabloko << apelsin << banan << limon;
            langs << eng << rus;
            generators << g1;
            results << res1 << res2 << res3;

            def all = [langs, generators, phrases, translations, tests, results];
            all.each{
                it.each{
                    ks.saveEntity(it);
                }
            }
        })
    }

    def static clearDb(KernelService ks){
        ks.doInTransaction({
            ks.deleteEntitiesByQuery(Result.class, "select * from results", null);
            ks.deleteEntitiesByQuery(Test.class, "select * from tests", null);
            ks.deleteEntitiesByQuery(Task.class, "select * from tasks", null);
            ks.deleteEntitiesByQuery(Translation.class, "select * from translations", null);
            ks.deleteEntitiesByQuery(Phrase.class, "select * from phrases", null);
            ks.deleteEntitiesByQuery(Generator.class, "select * from generators", null);
            ks.deleteEntitiesByQuery(Lang.class, "select * from langs", null);
        })
    }
}
