package com.mephiboys.satia.kernel.impl.generator;

import com.mephiboys.satia.kernel.impl.berkley.BerkeleyParserFactory;
import com.mephiboys.satia.kernel.impl.berkley.ParserHolder;
import com.mephiboys.satia.kernel.impl.entitiy.Task;
import edu.berkeley.nlp.syntax.Tree;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.features.NumberAgreement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.*;
import simplenlg.realiser.english.Realiser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.Random;

abstract public class AbstractParsingAnswerGenerator extends AbstractAnswerGenerator{
	protected static final String VERB = "vb";
    protected static final String ADJECTIVE = "adj";
    protected static final String NOUN = "n";
    protected static final String ADVERB = "adv";
    protected static final String PREPOSITION = "prp";
    protected static final String PUNCTUATION_SIGNS = ".,:;?!";
    protected static final Map<String, String> PARTS_OF_SPEECH = new HashMap<>();
    
    //words for substitution
    protected static final String[] VERBS = {"fly", "run", "swim", "think"};
    protected static final String[] ADJECTIVES = {"green", "blue", "big", "small"};
    protected static final String[] NOUNS = {"orange", "chair", "tree", "lighter"};
    protected static final String[] ADVERBS = {"fast", "deeply", "simply"};
    protected static final String[] PREPOSITIONS = {"on", "in", "at", "of"};

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
    protected static Lexicon lexicon = Lexicon.getDefaultLexicon();
    protected static NLGFactory nlgFactory = new NLGFactory(lexicon);
    protected static Realiser realiser = new Realiser(lexicon);
    protected static Random random = new Random();
    protected ThreadLocal<Integer> wordsReplaced = new ThreadLocal<>();


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
        try {
        	return handleTree(tree, params);
        } catch (Exception e) {
        	return new ArrayList<String>();
        }
    }

    protected void passTree(List<Tree<String>> tree, Map<String, Object> params,
                            BiConsumer<Tree<String>, Map<String, Object>> action){
        tree.forEach(t -> {
            passTree(t.getChildren(), params, action);
            action.accept(t, params);
        });
    }

    protected List<String> handleTree(List<Tree<String>> tree, Map<String, Object> params) throws IllegalStateException {
        List<String> answersList = new ArrayList<>();
        for (int i = 0; i < ANSWER_COUNT; ++i) {
            String handledSentence = null;
            do {
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

    abstract protected void handleTreeNode(Tree<String> node, Map<String, Object> params);
    
    protected String getWord(String partOfSpeech) {
		if (partOfSpeech.equals(VERB)) {
			return VERBS[random.nextInt(VERBS.length)];
		} else if (partOfSpeech.equals(ADJECTIVE)) {
			return ADJECTIVES[random.nextInt(ADJECTIVES.length)];
		} else if (partOfSpeech.equals(NOUN)) {
			return NOUNS[random.nextInt(NOUNS.length)];
		} else if (partOfSpeech.equals(ADVERB)) {
			return ADVERBS[random.nextInt(ADVERBS.length)];
		} else if (partOfSpeech.equals(PREPOSITION)) {
			return PREPOSITIONS[random.nextInt(PREPOSITIONS.length)];
		} else {
			return "";
		}
	}
    
    private String postHandle(String phrase, Tree<String> node, boolean isProper) {
    	phrase = phrase.split("\\.")[0];
		if ( (!isProper) && (Character.isLowerCase(node.getChild(0).getLabel().charAt(0))) ) {
			phrase = phrase.toLowerCase();
		}
		return phrase;
    }
	
    protected void handleVerb(Tree<String> node) {
		String label = node.getLabel();
		if (!label.startsWith("V")) {//skip if not a verb
			return;
		}
		
		SPhraseSpec p = nlgFactory.createClause();
		p.setVerb(getWord(VERB));
		String newVerb = "";
		
		SPhraseSpec sourceVerbPhrase = nlgFactory.createClause();
		sourceVerbPhrase.setVerb(node.getChild(0).getLabel());
		sourceVerbPhrase.setFeature(Feature.FORM, Form.BARE_INFINITIVE);
		if (realiser.realiseSentence(sourceVerbPhrase).equals("Be.") ||
			realiser.realiseSentence(sourceVerbPhrase).equals("Do.")) {//skip if BE or DO
			return;
		}
		
		if (label.equals("VB") || label.equals("VBP")) {
			p.setFeature(Feature.FORM, Form.BARE_INFINITIVE);
			newVerb = realiser.realiseSentence(p);
		}
		else if (label.equals("VBD")) {
			p.setFeature(Feature.TENSE, Tense.PAST);
			newVerb = realiser.realiseSentence(p);
		}
		else if (label.equals("VBG")) {
			p.setFeature(Feature.FORM, Form.GERUND);
			newVerb = realiser.realiseSentence(p);
		}
		else if (label.equals("VBN")) {
			p.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			newVerb = realiser.realiseSentence(p);
		}
		else if (label.equals("VBZ")) {
			p.setSubject("he");
			p.setFeature(Feature.TENSE, Tense.PRESENT);
			newVerb = realiser.realiseSentence(p).split(" ")[1];
		}
		
		newVerb = postHandle(newVerb, node, false);
		
		node.getChild(0).setLabel(newVerb);
		
		wordsReplaced.set(new Integer(wordsReplaced.get().intValue() + 1));
	}
	
    protected void handleAdjective(Tree<String> node) {
		String label = node.getLabel();
		if (!label.startsWith("J")) {
			return;
		}
		
		AdjPhraseSpec p = nlgFactory.createAdjectivePhrase();
		String newAdjective = "";
		p.setAdjective(getWord(ADJECTIVE));
		
		if (label.equals("JJR")) {
			p.setFeature(Feature.IS_COMPARATIVE, true);
		}
		else if (label.equals("JJS")) {
			p.setFeature(Feature.IS_SUPERLATIVE, true);
		}
		
		newAdjective = realiser.realiseSentence(p);
		newAdjective = postHandle(newAdjective, node, false);
		
		node.getChild(0).setLabel(newAdjective);
		
		wordsReplaced.set(new Integer(wordsReplaced.get().intValue() + 1));
	}
	
    protected void handleNoun(Tree<String> node) {
    	String label = node.getLabel();
		if (!label.startsWith("NN")) {
			return;
		}
		
		NPPhraseSpec p = nlgFactory.createNounPhrase();
		String newNoun = "";
		p.setNoun(getWord(NOUN));
		boolean isProper = (label.equals("NNP") || label.equals("NNPS"));
		
		if (label.equals("NN") || label.equals("NNP")) {
			p.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
		}
		else if (label.equals("NNS") || label.equals("NNPS")) {
			p.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		}
		
		newNoun = realiser.realiseSentence(p);
		newNoun = postHandle(newNoun, node, isProper);
		
		node.getChild(0).setLabel(newNoun);
		
		wordsReplaced.set(new Integer(wordsReplaced.get().intValue() + 1));
	}
	
    protected void handleAdverb(Tree<String> node) {
    	String label = node.getLabel();
		if (!label.startsWith("RB")) {
			return;
		}
		
		AdvPhraseSpec p = nlgFactory.createAdverbPhrase();
		String newAdverb = "";
		p.setAdverb(getWord(ADVERB));
		
		if (label.equals("RBR")) {
			p.setFeature(Feature.IS_COMPARATIVE, true);
		}
		else if (label.equals("RBS")) {
			p.setFeature(Feature.IS_SUPERLATIVE, true);
		}
		
		newAdverb = realiser.realiseSentence(p);
		newAdverb = postHandle(newAdverb, node, false);
		
		node.getChild(0).setLabel(newAdverb);
		
		wordsReplaced.set(new Integer(wordsReplaced.get().intValue() + 1));
	}
	
    protected void handlePreposition(Tree<String> node) {
    	String label = node.getLabel();
		if (!label.startsWith("IN")) {
			return;
		}
		
		String newPreposition = getWord(PREPOSITION);
		
		node.getChild(0).setLabel(newPreposition);
		
		wordsReplaced.set(new Integer(wordsReplaced.get().intValue() + 1));
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
