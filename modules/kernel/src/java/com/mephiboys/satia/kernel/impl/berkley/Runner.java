package com.mephiboys.satia.kernel.impl.berkley;


import com.mephiboys.satia.kernel.api.AnswerGenerator;
import com.mephiboys.satia.kernel.impl.entitiy.*;
//import com.mephiboys.satia.kernel.impl.generator.WordOrderReplaceGenerator;
import com.mephiboys.satia.kernel.impl.generator.PartOfSpeechReplacer;
import edu.berkeley.nlp.syntax.Tree;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Runner {

    public static void main(String[] args) {

        Lang rus = new Lang(); rus.setLang("rus");
        Lang eng = new Lang(); eng.setLang("eng");

        Translation translation = new Translation();
        Phrase p1 = new Phrase(); p1.setLang(rus); p1.setValue("ѕопроси белого медвед€ быстро принести водку в дом");
        Phrase p2 = new Phrase(); p2.setLang(eng); p2.setValue("Ask the white bear to bring some vodka quickly in the house");

        translation.setPhrase1(p1);
        translation.setPhrase2(p2);

        Task task = new Task();
        task.setTranslation(translation);
        task.setSourceNum((byte)1);

        /*Field pos = new Field();
        pos.setName("pos");
        pos.setInternalName("pos");

        FieldValue val = new FieldValue();
        val.setField(pos);
        val.setValue("5");

        AnswerGenerator generator = new WordOrderReplaceGenerator();
        List<String> generated = generator.generate(p1.getValue(), p2.getValue(), task, Arrays.asList(val));
        System.out.println(generated);*/

        Field pSpch = new Field();
        pSpch.setName("part of speech");
        pSpch.setInternalName("part_of_speech");
        
        FieldValue pSpchVal = new FieldValue();
        pSpchVal.setField(pSpch);
        pSpchVal.setValue("vb");
        
        AnswerGenerator generator = new PartOfSpeechReplacer();
        List<String> generated = generator.generate(p1.getValue(), p2.getValue(), task, Arrays.asList(pSpchVal));
        System.out.println(generated);
    }

    public static void passTree(List<Tree<String>> tree, Consumer<Tree<String>> action){
        tree.forEach(t -> {
            passTree(t.getChildren(), action);
            action.accept(t);
        });
    }
}
