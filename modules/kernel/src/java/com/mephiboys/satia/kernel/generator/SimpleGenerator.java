package com.mephiboys.satia.kernel.generator;


import com.mephiboys.satia.kernel.impl.entitiy.Task;

import java.util.*;

public class SimpleGenerator extends AbstractAnswerGenerator {
    @Override
    public List<String> generate(String source, String translation, Task task, Map<String, Object> params) {
        return randomizeAnswers((List)params.get("answers"));
    }

}
