package com.mephiboys.satia.kernel.test
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*

class DbAction {

    def static fillDb(KernelService ks){

        def user = ks.getEntityById(User.class, "sadmin")

        def tests = []
        def tasks = []
        def translations = []
        def phrases = []
        def generators = []
        def fields = []
        def fieldValues = []
        def results = []
        def langs = []

        ks.doInTransaction({
            Lang eng = new Lang(lang:"eng")
            Lang rus = new Lang(lang:"rus")
            langs << eng << rus;
            for (o in langs){
                ks.saveEntity(o)
            }

            Generator g1 = new Generator(
                    impl: "com.mephiboys.satia.kernel.impl.generator.SimpleGenerator"
            )
            generators << g1;
            for (o in generators){
                ks.saveEntity(o)
            }

            Phrase apple = new Phrase(value: "Apple", lang: eng)
            Phrase yabloko = new Phrase(value: "������", lang: rus)
            Phrase orange = new Phrase(value: "Orange", lang: eng)
            Phrase apelsin = new Phrase(value: "��������", lang: rus)
            Phrase banana = new Phrase(value: "Banana", lang: eng)
            Phrase banan = new Phrase(value: "�����", lang: rus)
            Phrase lemon = new Phrase(value: "Lemon", lang: eng)
            Phrase limon = new Phrase(value: "�����", lang: rus)
            phrases << apple << orange << banana << lemon << yabloko << apelsin << banan << limon;
            for (o in phrases){
                ks.saveEntity(o)
            }

            Translation tr1 = new Translation(phrase1: apple, phrase2: yabloko)
            Translation tr2 = new Translation(phrase1: orange, phrase2: apelsin)
            Translation tr3 = new Translation(phrase1: banana, phrase2: banan)
            Translation tr4 = new Translation(phrase1: lemon, phrase2: limon)
            translations << tr1 << tr2 << tr3 << tr4;
            for (o in translations){
                ks.saveEntity(o)
            }

            Task task1 = new Task(translation: tr1, sourceNum: 2)
            Task task2 = new Task(translation: tr2, sourceNum: 2)
            Task task3 = new Task(translation: tr3, sourceNum: 2)
            Task task4 = new Task(translation: tr4, sourceNum: 2)
            tasks << task1 << task2 << task3 << task4;
            for (o in tasks){
                ks.saveEntity(o)
            }

            Field f1 = new Field(generator : g1, name : "answers", internalName: "answers", showOrder: 1, multiple : true)
            fields << f1;
            for (o in fields){
                ks.saveEntity(o)
            }

            FieldValue fv1 = new FieldValue(field : f1, task : task1, value : "Pineapple")
            FieldValue fv2 = new FieldValue(field : f1, task : task1, value : "Grape")
            FieldValue fv3 = new FieldValue(field : f1, task : task1, value : "Tomato")
            fieldValues << fv1 << fv2 << fv3;
            for (o in fieldValues){
                ks.saveEntity(o)
            }

            Test test1 = new Test(
                    title: "Fruit test",
                    description: "Try to pass the simplest test ever",
                    createdWhen: new Date().toTimestamp(),
                    user: user,
                    generator: g1,
                    tasks: [task1, task2, task3, task4],
                    sourceLang: rus,
                    targetLang: eng
            )
            tests << test1;
            for (o in tests){
                ks.saveEntity(o)
            }

            Result res1 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey1",
                    value : 50.00, test : test1, user : null);
            Result res2 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey2",
                    value : 60.00, test : test1, user : user);
            Result res3 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey3",
                    value : 70.00, test : test1, user : user);
            results << res1 << res2 << res3;
            for (o in results){
                ks.saveEntity(o)
            }

        })

    }

    def static clearDb(KernelService ks){
        ks.doInTransaction({
            ks.deleteEntitiesByQuery(Result.class, "select * from results", null);
            ks.deleteEntitiesByQuery(Test.class, "select * from tests", null);
            ks.deleteEntitiesByQuery(FieldValue.class, "select * from field_values", null);
            ks.deleteEntitiesByQuery(Field.class, "select * from fields", null);
            ks.deleteEntitiesByQuery(Task.class, "select * from tasks", null);
            ks.deleteEntitiesByQuery(Translation.class, "select * from translations", null);
            ks.deleteEntitiesByQuery(Phrase.class, "select * from phrases", null);
            ks.deleteEntitiesByQuery(Generator.class, "select * from generators", null);
            ks.deleteEntitiesByQuery(Lang.class, "select * from langs", null);
        })
    }
}
