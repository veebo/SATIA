package com.mephiboys.satia.kernel.impl.berkley;


import edu.berkeley.nlp.PCFGLA.BerkeleyParser.Options;
import edu.berkeley.nlp.PCFGLA.*;
import edu.berkeley.nlp.ui.TreeJPanel;
import edu.berkeley.nlp.util.Numberer;

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


}
