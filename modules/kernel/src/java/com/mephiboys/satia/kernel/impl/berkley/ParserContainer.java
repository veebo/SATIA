package com.mephiboys.satia.kernel.impl.berkley;


import edu.berkeley.nlp.PCFGLA.*;
import edu.berkeley.nlp.io.PTBLineLexer;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.ui.TreeJPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParserContainer {

    private CoarseToFineMaxRuleParser parser;
    private MultiThreadedParserWrapper mparser;
    private BerkeleyParser.Options opts;

    public ParserContainer(CoarseToFineMaxRuleParser parser, MultiThreadedParserWrapper mparser, BerkeleyParser.Options options) {
        this.parser = parser;
        this.mparser = mparser;
        this.opts = options;
    }

    public CoarseToFineMaxRuleParser getParser() {
        return parser;
    }

    public void setParser(CoarseToFineMaxRuleProductParser parser) {
        this.parser = parser;
    }

    public MultiThreadedParserWrapper getMparser() {
        return mparser;
    }

    public void setMparser(MultiThreadedParserWrapper mparser) {
        this.mparser = mparser;
    }

    public BerkeleyParser.Options getOpts() {
        return opts;
    }

    public void setOpts(BerkeleyParser.Options opts) {
        this.opts = opts;
    }

    public List<Tree<String>> parse(String line){

        PrintWriter outputData = null;
        List<Tree<String>> parsedTrees = null;

        try {
            if (/*StringUtils.isEmpty(line)*/line == null && line.isEmpty()){
                return null;
            }
            PTBLineLexer tokenizer = null;
            if (opts.tokenize)
                tokenizer = new PTBLineLexer();

            String sentenceID = "";
            line = line.trim();
            if (opts.ec_format && line.equals(""))
                return null;
            List<String> sentence = null;
            List<String> posTags = null;
            if (opts.goldPOS) {
                sentence = new ArrayList<String>();
                posTags = new ArrayList<String>();
                List<String> tmp = Arrays.asList(line.split("\t"));
                if (tmp.size() == 0)
                    return null;

                sentence.add(tmp.get(0));
                String[] tags = tmp.get(1).split("-");
                posTags.add(tags[0]);
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

            outputData = new PrintWriter(new ByteArrayOutputStream());


            if (sentence.size() > opts.maxLength) {
                outputData.write("(())\n");
                if (opts.kbest > 1) {
                    outputData.write("\n");
                }
                System.err.println("Skipping sentence with "
                        + sentence.size() + " words since it is too long.");
                return null;
            }

            if (opts.nThreads > 1) {
                mparser.parseThisSentence(sentence);
                while (mparser.hasNext()) {
                    parsedTrees = mparser.getNext();
                    outputTrees(parsedTrees, outputData, parser, opts, "",
                            sentenceID);
                }
            } else {
                parsedTrees = null;
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
                outputTrees(parsedTrees, outputData, parser, opts, line, sentenceID);
            }

            if (opts.nThreads > 1) {
                while (!mparser.isDone()) {
                    while (mparser.hasNext()) {
                        parsedTrees = mparser.getNext();
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

        return parsedTrees;
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

    public static void writeTreeToImage(Tree<String> tree, String fileName) throws IOException {

        if (BerkeleyParserFactory.PANEL == null)
            BerkeleyParserFactory.PANEL = new TreeJPanel();
        BerkeleyParserFactory.PANEL.setTree(tree);

        BufferedImage bi = new BufferedImage(BerkeleyParserFactory.PANEL.width(), BerkeleyParserFactory.PANEL.height(),
                BufferedImage.TYPE_INT_ARGB);
        int t = BerkeleyParserFactory.PANEL.height();
        Graphics2D g2 = bi.createGraphics();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1.0f));
        Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, BerkeleyParserFactory.PANEL.width(),
                BerkeleyParserFactory.PANEL.height());
        g2.fill(rect);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        BerkeleyParserFactory.PANEL.paintComponent(g2); // paint the graphic to the offscreen image
        g2.dispose();

        ImageIO.write(bi, "png", new File(fileName)); // save as png format
        // DONE!
    }
}
