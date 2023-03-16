package com.searchengine;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static String sourceFilePath = "ressources/frwiki-20230301-pages-articles.xml";
    static String sourceFilePathShort = "ressources/frwiki10000.xml";
    static String testInput = "ressources/input.xml";
    static String corpusPath = "ressources/corpus.xml";
    static String matrixCLIPath = "ressources/CLI.txt";
    public static void main(String[] args) {


//        BuildCorpus buildCorpus = new BuildCorpus(sourceFilePath, corpusPath);
//        buildCorpus.buildCorpus();

//        System.out.println(BuildCorpus.calculatePageNumber(corpusPath));

//        BuildCorpus buildDicoAndWordRelation = new BuildCorpus(corpusPath, "");
//        buildDicoAndWordRelation.buildDico();
//////        you must execute first b.buildDico() to execute b.buildWordRelation()
//        buildDicoAndWordRelation.buildWordRelation();

//        // build CLI Matrix
//        BuildCLI b = new BuildCLI(corpusPath, matrixCLIPath);
//        b.buildCLI();

    }
}
