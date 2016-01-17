package com.mephiboys.satia.kernel.impl.generator;

import com.mephiboys.satia.kernel.api.KernelHelper;
import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import edu.berkeley.nlp.syntax.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

abstract public class AbstractParsingAnswerGenerator extends AbstractAnswerGenerator{

    protected final KernelService ks = KernelHelper.getKernelService();

    @Override
    protected List<String> generate(String source, String translation, Task task, Map<String, Object> params) {
        List<Tree<String>> tree = ks.getParserHolder().parse(
                task.getSourceNum() == 1
                        ? task.getTranslation().getPhrase1().getLang().getLang()
                        : task.getTranslation().getPhrase2().getLang().getLang(),
                translation
        );
        return handleTree(tree);
    }

    private void passTree(List<Tree<String>> tree, Consumer<Tree<String>> action){
        tree.forEach(t -> {
            passTree(t.getChildren(), action);
            action.accept(t);
        });
    }

    protected List<String> handleTree(List<Tree<String>> tree){
        List<String> answersList = new ArrayList<>();
        for (int i = 0; i < ANSWER_COUNT; ++i) {
            passTree(tree, this::handleTreeNode);
            StringBuilder builder = new StringBuilder();
            tree.iterator().forEachRemaining(t -> t.iterator().forEachRemaining(
                    node -> {if (node.isLeaf()) builder.append(node.getLabel()).append(" ");}
            ));
            answersList.add(builder.toString());
        }
        return answersList;
    }

    abstract protected void handleTreeNode(Tree<String> node);
}
