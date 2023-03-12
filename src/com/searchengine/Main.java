package com.searchengine;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Main {
    public static void main(String[] args) {
        String sourceFilePath = "ressources/frwiki-20230301-pages-articles.xml";
        String sourceFilePathShort = "ressources/frwiki10000.xml";
        String corpusPath = "ressources/corpus.xml";
        String corpusPats = "ressources/corpus.xml";

//        BuildCorpus b = new BuildCorpus(sourceFilePath, corpusPath);
//        b.buildCorpus();

//        BuildCorpus.calculatePageNumber(corpusPath);


    }
}
