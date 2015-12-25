package com.mephiboys.satia.kernel.generator;


import com.mephiboys.satia.kernel.api.KernelHelper;
import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.Phrase;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import com.mephiboys.satia.kernel.impl.entitiy.Test;

import java.util.*;
import java.util.stream.Collectors;

public class TestPhraseGenerator extends AbstractAnswerGenerator{

    private final KernelService ks = KernelHelper.getKernelService();

    public List<String> generate(String source, String translation, Task task, Map<String, Object> params) {
        Test test = task.getTests().get(0);
        Collection<Phrase> phrases = ks.getEntitiesByQuery(
                Phrase.class,
                "select phrase_id from phrases p \n" +
                "\tjoin translations tr on (p.lang = ? and (tr.phrase1_id = p.phrase_id or tr.phrase2_id = p.phrase_id) )\n" +
                "\tjoin tasks t on (t.translation_id = tr.translation_id)\n" +
                "\tjoin test_tasks tt on (t.task_id = tt.task_id)\n" +
                "\twhere tt.test_id = ?",
                test.getTargetLang().getLang(),
                test.getTestId()
        );
        phrases.remove(translation);
        return randomizeAnswers(phrases.stream().map(p -> p.getValue()).filter(v -> !v.equals(translation)).collect(Collectors.toList()));
    }
}
