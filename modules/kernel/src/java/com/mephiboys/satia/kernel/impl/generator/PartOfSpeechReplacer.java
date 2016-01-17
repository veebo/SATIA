package com.mephiboys.satia.kernel.impl.generator;

import edu.berkeley.nlp.syntax.Tree;

import java.util.Map;

public class PartOfSpeechReplacer extends AbstractParsingAnswerGenerator {
	
	@Override
	protected void handleTreeNode(Tree<String> node, Map<String, Object> params) {
		String partOfSpeech = (String)params.get("part_of_speech");
		if ((!node.isPreTerminal()) || (partOfSpeech == null)) {
			return;
		}
		
		if (partOfSpeech.equals(VERB)) {
			handleVerb(node);
		} else if (partOfSpeech.equals(ADJECTIVE)) {
			handleAdjective(node);
		} else if (partOfSpeech.equals(NOUN)) {
			handleNoun(node);
		} else if (partOfSpeech.equals(ADVERB)) {
			handleAdverb(node);
		}  else if (partOfSpeech.equals(PREPOSITION)) {
			handlePreposition(node);
		}
	}
	
}
