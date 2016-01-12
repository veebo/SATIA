package com.mephiboys.satia.kernel.impl.generator;


import com.mephiboys.satia.kernel.impl.entitiy.Task;

import java.util.*;

public class SimpleGenerator extends AbstractAnswerGenerator {
    @Override
    public List<String> generate(String source, String translation, Task task, Map<String, Object> params) {
        List<String> answersList = null;
        Object answers = params.get("answers");
        if (answers == null) {
        	answersList = new ArrayList<String>();
        } else {
        	answersList = (List)answers;
        }
        return randomizeAnswers(answersList);
    }

}
