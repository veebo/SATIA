package com.mephiboys.satia.kernel.generator;


import com.mephiboys.satia.kernel.impl.entitiy.Field;
import com.mephiboys.satia.kernel.impl.entitiy.FieldValue;
import com.mephiboys.satia.kernel.impl.entitiy.Task;

import java.util.*;

abstract public class AbstractAnswerGenerator implements AnswerGenerator {

    public static final int ANSWER_COUNT = 4;

    @Override
    public List<String> generate(String source, String translation, Task task) {
        Map<String, Object> params = new HashMap<>();
        for (FieldValue v : task.getFieldValues()){
            Field f = v.getField();
            String name = f.getInternalName();
            Object param = params.get(name);
            if (param == null){
                params.put(name, f.isMultiple() ? new ArrayList<>(Arrays.asList(v.getValue())) : v.getValue());
            } else {
                if (f.isMultiple()){
                    ((List)params.get(name)).add(v.getValue());
                } else {
                    params.put(name, v.getValue());
                }
            }
        }
        return generate(source, translation, task, params);
    }

    protected List<String> randomizeAnswers(List<String> answers){
        int size = answers.size();
        List<String> result = new ArrayList<>();
        Collections.shuffle(answers);
        for (int i = 0; i < ANSWER_COUNT - 1; ++i) {
            if (i == size){
                break;
            }
            result.add(answers.get(i));
        }
        return result;
    }

    abstract protected List<String> generate(String source, String translation, Task task, Map<String, Object> params);

}
