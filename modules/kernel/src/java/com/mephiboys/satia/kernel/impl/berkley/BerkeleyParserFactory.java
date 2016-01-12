package com.mephiboys.satia.kernel.impl.berkley;


import edu.berkeley.nlp.PCFGLA.*;
import edu.berkeley.nlp.PCFGLA.BerkeleyParser.Options;
import edu.berkeley.nlp.io.PTBLineLexer;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.ui.TreeJPanel;
import edu.berkeley.nlp.util.Numberer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BerkeleyParserFactory {
    public static TreeJPanel PANEL;

    public static ParserContainer create(String[] args) {
        OptionParser optParser = new OptionParser(Options.class);
        Options opts = (Options) optParser.parse(args, true);

        double threshold = 1.0;

        if (opts.chinese)
            Corpus.myTreebank = Corpus.TreeBankType.CHINESE;

        CoarseToFineMaxRuleParser parser = null;
        if (opts.nGrammars != 1) {
            Grammar[] grammars = new Grammar[opts.nGrammars];
            Lexicon[] lexicons = new Lexicon[opts.nGrammars];
            Binarization bin = null;
            for (int nGr = 0; nGr < opts.nGrammars; nGr++) {
                String inFileName = opts.grFileName + "." + nGr;
                ParserData pData = ParserData.Load(inFileName);
                if (pData == null) {
                    System.out.println("Failed to load grammar from file"
                            + inFileName + ".");
                    System.exit(1);
                }
                grammars[nGr] = pData.getGrammar();
                lexicons[nGr] = pData.getLexicon();
                Numberer.setNumberers(pData.getNumbs());
                bin = pData.getBinarization();
            }
            parser = new CoarseToFineMaxRuleProductParser(grammars, lexicons,
                    threshold, -1, opts.viterbi, opts.substates, opts.scores,
                    opts.accurate, opts.variational, true, true);
            parser.binarization = bin;
        } else {
            String inFileName = opts.grFileName;
            ParserData pData = ParserData.Load(inFileName);
            if (pData == null) {
                System.out.println("Failed to load grammar from file"
                        + inFileName + ".");
                System.exit(1);
            }
            Grammar grammar = pData.getGrammar();
            Lexicon lexicon = pData.getLexicon();
            Numberer.setNumberers(pData.getNumbs());
            if (opts.kbest == 1)
                parser = new CoarseToFineMaxRuleParser(grammar, lexicon,
                        threshold, -1, opts.viterbi, opts.substates,
                        opts.scores, opts.accurate, opts.variational, true,
                        true);
            else
                parser = new CoarseToFineNBestParser(grammar, lexicon,
                        opts.kbest, threshold, -1, opts.viterbi,
                        opts.substates, opts.scores, opts.accurate,
                        opts.variational, false, true);
            parser.binarization = pData.getBinarization();
        }


        MultiThreadedParserWrapper m_parser = null;
        if (opts.nThreads > 1) {
            System.err.println("Parsing with " + opts.nThreads
                    + " threads in parallel.");
            m_parser = new MultiThreadedParserWrapper(parser, opts.nThreads);
        }
        return new ParserContainer(parser, m_parser, opts);
    }

    public static void parse(Options opts, CoarseToFineMaxRuleParser parser, MultiThreadedParserWrapper m_parser){
        try {

            BufferedReader inputData = (opts.inputFile == null) ? new BufferedReader(
                    new InputStreamReader(System.in)) : new BufferedReader(
                    new InputStreamReader(new FileInputStream(opts.inputFile),
                            "UTF-8"));
            PrintWriter outputData = (opts.outputFile == null) ? new PrintWriter(
                    new OutputStreamWriter(System.out)) : new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(opts.outputFile), "UTF-8"),
                    true);
            PTBLineLexer tokenizer = null;
            if (opts.tokenize)
                tokenizer = new PTBLineLexer();

            String line = "";
            String sentenceID = "";
            while ((line = inputData.readLine()) != null) {
                line = line.trim();
                if (opts.ec_format && line.equals(""))
                    continue;
                List<String> sentence = null;
                List<String> posTags = null;
                if (opts.goldPOS) {
                    sentence = new ArrayList<String>();
                    posTags = new ArrayList<String>();
                    List<String> tmp = Arrays.asList(line.split("\t"));
                    if (tmp.size() == 0)
                        continue;
                    // System.out.println(line+tmp);
                    sentence.add(tmp.get(0));
                    String[] tags = tmp.get(1).split("-");
                    posTags.add(tags[0]);
                    while (!(line = inputData.readLine()).equals("")) {
                        tmp = Arrays.asList(line.split("\t"));
                        if (tmp.size() == 0)
                            break;
                        // System.out.println(line+tmp);
                        sentence.add(tmp.get(0));
                        tags = tmp.get(1).split("-");
                        posTags.add(tags[0]);
                    }
                } else {
                    if (opts.ec_format) {
                        int breakIndex = line.indexOf(">");
                        sentenceID = line.substring(3, breakIndex - 1);
                        line = line
                                .substring(breakIndex + 2, line.length() - 5);
                    }
                    if (!opts.tokenize)
                        sentence = Arrays.asList(line.split("\\s+"));
                    else {
                        sentence = tokenizer.tokenizeLine(line);
                    }
                }

                // if (sentence.size()==0) { outputData.write("\n"); continue;
                // }//break;
                if (sentence.size() > opts.maxLength) {
                    outputData.write("(())\n");
                    if (opts.kbest > 1) {
                        outputData.write("\n");
                    }
                    System.err.println("Skipping sentence with "
                            + sentence.size() + " words since it is too long.");
                    continue;
                }

                if (opts.nThreads > 1) {
                    m_parser.parseThisSentence(sentence);
                    while (m_parser.hasNext()) {
                        List<Tree<String>> parsedTrees = m_parser.getNext();
                        outputTrees(parsedTrees, outputData, parser, opts, "",
                                sentenceID);
                    }
                } else {
                    List<Tree<String>> parsedTrees = null;
                    if (opts.kbest > 1) {
                        parsedTrees = parser.getKBestConstrainedParses(
                                sentence, posTags, opts.kbest);
                        if (parsedTrees.size() == 0) {
                            parsedTrees.add(new Tree<String>("ROOT"));
                        }
                    } else {
                        parsedTrees = new ArrayList<Tree<String>>();
                        Tree<String> parsedTree = parser
                                .getBestConstrainedParse(sentence, posTags,
                                        null);
                        if (opts.goldPOS && parsedTree.getChildren().isEmpty()) { // parse
                            // error
                            // when
                            // using
                            // goldPOS,
                            // try
                            // without
                            parsedTree = parser.getBestConstrainedParse(
                                    sentence, null, null);
                        }
                        parsedTrees.add(parsedTree);

                    }
                    outputTrees(parsedTrees, outputData, parser, opts, line,
                            sentenceID);
                }
            }
            if (opts.nThreads > 1) {
                while (!m_parser.isDone()) {
                    while (m_parser.hasNext()) {
                        List<Tree<String>> parsedTrees = m_parser.getNext();
                        outputTrees(parsedTrees, outputData, parser, opts,
                                line, sentenceID);
                    }
                }
            }
            if (opts.dumpPosteriors) {
                String fileName = opts.grFileName + ".posteriors";
                parser.dumpPosteriors(fileName, -1);
            }
            outputData.flush();
            outputData.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private static void outputTrees(List<Tree<String>> parseTrees,
                                    PrintWriter outputData, CoarseToFineMaxRuleParser parser,
                                    edu.berkeley.nlp.PCFGLA.BerkeleyParser.Options opts, String line,
                                    String sentenceID) {
        String delimiter = "\t";
        if (opts.ec_format) {
            List<Tree<String>> newList = new ArrayList<Tree<String>>(
                    parseTrees.size());
            for (Tree<String> parsedTree : parseTrees) {
                if (parsedTree.getChildren().isEmpty())
                    continue;
                if (parser.getLogLikelihood(parsedTree) != Double.NEGATIVE_INFINITY) {
                    newList.add(parsedTree);
                }
            }
            parseTrees = newList;
        }
        if (opts.ec_format) {
            outputData.write(parseTrees.size() + "\t" + sentenceID + "\n");
            delimiter = ",\t";
        }

        for (Tree<String> parsedTree : parseTrees) {
            boolean addDelimiter = false;
            if (opts.tree_likelihood) {
                double treeLL = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
                        : parser.getLogLikelihood(parsedTree);
                if (treeLL == Double.NEGATIVE_INFINITY)
                    continue;
                outputData.write(treeLL + "");
                addDelimiter = true;
            }
            if (opts.sentence_likelihood) {
                double allLL = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
                        : parser.getLogLikelihood();
                if (addDelimiter)
                    outputData.write(delimiter);
                addDelimiter = true;
                if (opts.ec_format)
                    outputData.write("sentenceLikelihood ");
                outputData.write(allLL + "");
            }
            if (!opts.binarize)
                parsedTree = TreeAnnotations.unAnnotateTree(parsedTree,
                        opts.keepFunctionLabels);
            if (opts.confidence) {
                double treeLL = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
                        : parser.getConfidence(parsedTree);
                if (addDelimiter)
                    outputData.write(delimiter);
                addDelimiter = true;
                if (opts.ec_format)
                    outputData.write("confidence ");
                outputData.write(treeLL + "");
            } else if (opts.modelScore) {
                double score = (parsedTree.getChildren().isEmpty()) ? Double.NEGATIVE_INFINITY
                        : parser.getModelScore(parsedTree);
                if (addDelimiter)
                    outputData.write(delimiter);
                addDelimiter = true;
                if (opts.ec_format)
                    outputData.write("maxRuleScore ");
                outputData.write(String.format("%.8f", score));
            }
            if (opts.ec_format)
                outputData.write("\n");
            else if (addDelimiter)
                outputData.write(delimiter);
            if (!parsedTree.getChildren().isEmpty()) {
                String treeString = parsedTree.getChildren().get(0).toString();
                if (parsedTree.getChildren().size() != 1) {
                    System.err.println("ROOT has more than one child!");
                    parsedTree.setLabel("");
                    treeString = parsedTree.toString();
                }
                if (opts.ec_format)
                    outputData.write("(S1 " + treeString + " )\n");
                else
                    outputData.write("( " + treeString + " )\n");
            } else {
                outputData.write("(())\n");
            }
            if (opts.render)
                try {
                    writeTreeToImage(parsedTree,
                            line.replaceAll("[^a-zA-Z]", "") + ".png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        if (opts.dumpPosteriors) {
            int blockSize = 50;
            String fileName = opts.grFileName + ".posteriors";
            parser.dumpPosteriors(fileName, blockSize);
        }

        if (opts.kbest > 1)
            outputData.write("\n");
        outputData.flush();

    }

    private static void writeTreeToImage(Tree<String> tree, String fileName) throws IOException {

        if (PANEL == null)
            PANEL = new TreeJPanel();
        PANEL.setTree(tree);

        BufferedImage bi = new BufferedImage(PANEL.width(), PANEL.height(),
                BufferedImage.TYPE_INT_ARGB);
        int t = PANEL.height();
        Graphics2D g2 = bi.createGraphics();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
        Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, PANEL.width(), PANEL.height());
        g2.fill(rect);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        PANEL.paintComponent(g2); // paint the graphic to the offscreen image
        g2.dispose();

        ImageIO.write(bi, "png", new File(fileName)); // save as png format
        // DONE!
    }
}
