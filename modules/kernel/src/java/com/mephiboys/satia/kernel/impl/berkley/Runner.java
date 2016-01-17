package com.mephiboys.satia.kernel.impl.berkley;


import edu.berkeley.nlp.syntax.Tree;

import java.util.List;
import java.util.function.Consumer;

public class Runner {

    public static void main(String[] args) {
        ParserHolder.INSTANCE.register("eng", BerkeleyParserFactory.create(new String[]{"-gr", "eng_sm6.gr"}));
        List<Tree<String>> tree = ParserHolder.INSTANCE.parse("eng",
                "Natural language processing is a field of computer science");
        StringBuilder builder = new StringBuilder();
        tree.iterator().forEachRemaining(t -> t.iterator().forEachRemaining(
                node -> {
                    if (node.isLeaf()) builder.append(node.getLabel()+" ");}
        ));
        System.out.println(builder.toString());

    }

    public static void passTree(List<Tree<String>> tree, Consumer<Tree<String>> action){
        tree.forEach(t -> {
            passTree(t.getChildren(), action);
            action.accept(t);
        });
    }
}
