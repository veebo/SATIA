package com.mephiboys.satia.kernel.impl.berkley;

import edu.berkeley.nlp.syntax.Tree;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParserHolder {

    private ParserHolder(){}

    public static final ParserHolder INSTANCE = new ParserHolder();

    private Map<String,ParserContainer> parsers = new ConcurrentHashMap<>();

    public void register(String lang, ParserContainer parser){
        parsers.put(lang, parser);
    }

    public List<Tree<String>> parse(String lang, String line){
        ParserContainer parser = parsers.get(lang);
        if (parser == null){
            throw new UnsupportedOperationException("No parser for '"+lang+"' language found");
        }
        return parser.parse(line);
    }
}