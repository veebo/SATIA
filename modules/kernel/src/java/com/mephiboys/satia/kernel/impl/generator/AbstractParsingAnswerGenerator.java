package com.mephiboys.satia.kernel.impl.generator;

import com.mephiboys.satia.kernel.impl.berkley.BerkeleyParserFactory;
import com.mephiboys.satia.kernel.impl.berkley.ParserHolder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract public class AbstractParsingAnswerGenerator extends AbstractAnswerGenerator{
	protected static final String VERB = "vb";
    protected static final String ADJECTIVE = "adj";
    protected static final String NOUN = "n";
    protected static final String ADVERB = "adv";
    protected static final String PREPOSITION = "prp";
    protected static final String PUNCTUATION_SIGNS = ".,:;?!";
    protected static final Map<String, String> PARTS_OF_SPEECH = new HashMap<>();

    static {
        PARTS_OF_SPEECH.put("VB", "vb");
        PARTS_OF_SPEECH.put("VBD", "vb");
        PARTS_OF_SPEECH.put("VBG", "vb");
        PARTS_OF_SPEECH.put("VBN", "vb");
        PARTS_OF_SPEECH.put("VBP", "vb");
        PARTS_OF_SPEECH.put("VBZ", "vb");
        //etc
    }
	
//	protected final KernelService ks = KernelHelper.getKernelService();
    protected Lexicon lexicon = Lexicon.getDefaultLexicon();
    protected NLGFactory nlgFactory = new NLGFactory(lexicon);
    protected Realiser realiser = new Realiser(lexicon);


    {
        ParserHolder.INSTANCE.register("eng", BerkeleyParserFactory.create(new String[]{"-gr", "eng_sm6.gr"}));
    }

    @Override
    protected List<String> generate(String source, String translation, Task task, Map<String, Object> params) {
    	List<Tree<String>> tree = /*ks.getParserHolder()*/
                ParserHolder.INSTANCE.parse(
                task.getSourceNum() == 2
                        ? task.getTranslation().getPhrase1().getLang().getLang()
                        : task.getTranslation().getPhrase2().getLang().getLang(),
                translation
        );
        return handleTree(tree, params);
    }

    protected void passTree(List<Tree<String>> tree, Map<String, Object> params,
                            BiConsumer<Tree<String>, Map<String, Object>> action){
        tree.forEach(t -> {
            passTree(t.getChildren(), params, action);
            action.accept(t, params);
        });
    }

    protected List<String> handleTree(List<Tree<String>> tree, Map<String, Object> params){
        List<String> answersList = new ArrayList<>();
        for (int i = 0; i < ANSWER_COUNT; ++i) {
            passTree(tree, params, this::handleTreeNode);
            StringBuilder builder = new StringBuilder();
            tree.iterator().forEachRemaining(t -> t.iterator().forEachRemaining(
                    node -> {if (node.isLeaf()) builder.append(node.getLabel()).append(" ");}
            ));
            answersList.add(builder.toString());
        }
        return answersList;
    }

    abstract protected void handleTreeNode(Tree<String> node, Map<String, Object> params);
    
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
		
		if (label.equals("VB") || label.equals("VBP")) {
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

    protected void handleAllowPunctuation(Tree<String> node, Consumer<Tree<String>> action){
        Tree<String> leaf = node.getChild(0);
        String label = leaf.getLabel().trim();
        String ending = label.substring(label.length() - 1);
        if (PUNCTUATION_SIGNS.contains(ending)){
            leaf.setLabel(label.substring(0, label.length() - 1));
            action.accept(node);
            leaf.setLabel(leaf.getLabel()+ending);
        } else {
            action.accept(node);
        }
    }

}
