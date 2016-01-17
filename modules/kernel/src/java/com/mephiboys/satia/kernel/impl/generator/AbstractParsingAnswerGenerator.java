package com.mephiboys.satia.kernel.impl.generator;

import com.mephiboys.satia.kernel.api.KernelHelper;
import com.mephiboys.satia.kernel.api.KernelService;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import edu.berkeley.nlp.syntax.Tree;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

abstract public class AbstractParsingAnswerGenerator extends AbstractAnswerGenerator{
	protected static final String VERB = "vb";
    protected static final String ADJECTIVE = "adj";
    protected static final String NOUN = "n";
    protected static final String ADVERB = "adv";
    protected static final String PREPOSITION = "prp";
	
	protected final KernelService ks = KernelHelper.getKernelService();
    protected Map<String, Object> params;
    protected Lexicon lexicon = Lexicon.getDefaultLexicon();
    protected NLGFactory nlgFactory = new NLGFactory(lexicon);
    protected Realiser realiser = new Realiser(lexicon);

    @Override
    protected List<String> generate(String source, String translation, Task task, Map<String, Object> params) {
        this.params = params;
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
    
    protected String getWord(String partOfSpeech) {
		if (partOfSpeech.equals(VERB)) {
			return "fly";
		} else if (partOfSpeech.equals(ADJECTIVE)) {
			return "green";
		} else if (partOfSpeech.equals(NOUN)) {
			return "weed";
		} else if (partOfSpeech.equals(ADVERB)) {
			return "clearly";
		} else if (partOfSpeech.equals(PREPOSITION)) {
			return "on";
		} else {
			return "";
		}
	}
	
    protected void handleVerb(Tree<String> node) {
		String label = node.getLabel();
		if (!label.startsWith("V")) {//skip if not a verb
			return;
		}
		
		SPhraseSpec p = nlgFactory.createClause();
		p.setVerb(getWord(VERB));
		String newVerbModified = "";
		
		SPhraseSpec sourceVerbPhrase = nlgFactory.createClause();
		sourceVerbPhrase.setVerb(node.getChild(0).getLabel());
		sourceVerbPhrase.setFeature(Feature.FORM, Form.BARE_INFINITIVE);
		if (realiser.realiseSentence(sourceVerbPhrase).equals("Be.") ||
			realiser.realiseSentence(sourceVerbPhrase).equals("Do.")) {//skip if BE or DO
			return;
		}
		
		if (label.equals("VB")) {
			p.setFeature(Feature.FORM, Form.BARE_INFINITIVE);
			newVerbModified = realiser.realiseSentence(p);
		}
		else if (label.equals("VBD")) {
			p.setFeature(Feature.TENSE, Tense.PAST);
			newVerbModified = realiser.realiseSentence(p);
		}
		else if (label.equals("VBG")) {
			p.setFeature(Feature.FORM, Form.GERUND);
			newVerbModified = realiser.realiseSentence(p);
		}
		else if (label.equals("VBN")) {
			p.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			newVerbModified = realiser.realiseSentence(p);
		}
		else if (label.equals("VBP")) {
			p.setSubject("You");
			p.setFeature(Feature.TENSE, Tense.PRESENT);
			newVerbModified = realiser.realiseSentence(p).split(" ")[1];
		}
		else if (label.equals("VBZ")) {
			p.setSubject("he");
			p.setFeature(Feature.TENSE, Tense.PRESENT);
			newVerbModified = realiser.realiseSentence(p).split(" ")[1];
		}
		
		newVerbModified = newVerbModified.split("\\.")[0];
		if (Character.isLowerCase(node.getChild(0).getLabel().charAt(0))) {
			newVerbModified = newVerbModified.toLowerCase();
		}
		
		node.getChild(0).setLabel(newVerbModified);
	}
	
    protected void handleAdjective(Tree<String> node) {
		//...
	}
	
    protected void handleNoun(Tree<String> node) {
		//...
	}
	
    protected void handleAdverb(Tree<String> node) {
		//...
	}
	
    protected void handlePreposition(Tree<String> node) {
		//...
	}
}
