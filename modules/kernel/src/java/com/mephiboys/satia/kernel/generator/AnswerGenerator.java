package com.mephiboys.satia.kernel.generator;

import com.mephiboys.satia.kernel.impl.entitiy.Task;

import java.util.List;

public interface AnswerGenerator {

    List<String> generate(String source, String translation, Task task);
}
