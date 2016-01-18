package com.mephiboys.satia.kernel.impl.generator;

import edu.berkeley.nlp.syntax.Tree;

import java.util.Map;
import java.util.function.Consumer;

public class PartOfSpeechReplacer extends AbstractParsingAnswerGenerator {
	
	@Override
	protected void handleTreeNode(Tree<String> node, Map<String, Object> params) {
		String partOfSpeech = (String)params.get("part_of_speech");
		if ((!node.isPreTerminal()) || (partOfSpeech == null)) {
			return;
		}
		
		Consumer<Tree<String>> action = null;
		if (partOfSpeech.equals(VERB)) {
			action = this::handleVerb;
		} else if (partOfSpeech.equals(ADJECTIVE)) {
			action = this::handleAdjective;
		} else if (partOfSpeech.equals(NOUN)) {
			action = this::handleNoun;
		} else if (partOfSpeech.equals(ADVERB)) {
			action = this::handleAdverb;
		}  else if (partOfSpeech.equals(PREPOSITION)) {
			action = this::handlePreposition;
		} else {
			return;
		}
		
		handleAllowPunctuation(node, action);
	}
	
}
