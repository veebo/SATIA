package com.mephiboys.satia.kernel.generator;

import com.mephiboys.satia.kernel.impl.entitiy.Task;
import com.mephiboys.satia.kernel.impl.entitiy.FieldValue;
import java.util.List;
import java.util.Collection;

public interface AnswerGenerator {

    List<String> generate(String source, String translation, Task task, Collection<FieldValue> fieldValues);
}
