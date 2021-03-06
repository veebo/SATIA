package com.mephiboys.satia.kernel.impl.generator;


import edu.berkeley.nlp.syntax.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WordOrderReplaceGenerator extends AbstractParsingAnswerGenerator {

    ThreadLocal<Integer> currentPos = new ThreadLocal<>();

    @Override
    protected List<String> handleTree(List<Tree<String>> tree, Map<String, Object> params)  throws IllegalStateException {
        List<String> answersList = new ArrayList<>();
        for (int i = 0; i < ANSWER_COUNT; ++i) {
            String handledSentence = null;
            do {
            	currentPos.set(0);
            	wordsReplaced.set(new Integer(0));
            	passTree(tree, params, this::handleTreeNode);
            	if (wordsReplaced.get().intValue() == 0) {
            		throw new IllegalStateException("Illegal generation parameters");
            	}
            	StringBuilder builder = new StringBuilder();
                tree.iterator().forEachRemaining(t -> t.iterator().forEachRemaining(
                        node -> {if (node.isLeaf()) builder.append(node.getLabel()).append(" ");}
                ));
                handledSentence = builder.toString();
            } while (answersList.contains(handledSentence));
            answersList.add(handledSentence);
        }
        return answersList;
    }

    @Override
    protected void handleTreeNode(Tree<String> node, Map<String, Object> params) {
        int pos = Integer.parseInt((String)params.get("pos"));

        if (node.isPreTerminal()){
            currentPos.set(currentPos.get() + 1);
            String partOfSpeech = PARTS_OF_SPEECH.get(node.getLabel());

            Consumer<Tree<String>> action = null;
            if (VERB.equals(partOfSpeech)) {
                action =this::handleVerb;
            } else if (ADJECTIVE.equals(partOfSpeech)) {
                action =this::handleAdjective;
            } else if (NOUN.equals(partOfSpeech)) {
                action =this::handleNoun;
            } else if (ADVERB.equals(partOfSpeech)) {
                action =this::handleAdverb;
            }  else if (PREPOSITION.equals(partOfSpeech)) {
                action =this:: handlePreposition;
            } else {
                return;
            }
            if (currentPos.get() == pos) {
                handleAllowPunctuation(node, action);
            }
        }
    }


}
