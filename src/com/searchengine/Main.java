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
    static String matrixCLIPathTest = "ressources/CLI2.txt";
    static String pageIdTitlePath = "ressources/pageid_title.txt";
    public static void main(String[] args) {


//        BuildCorpus buildCorpus = new BuildCorpus(sourceFilePath, corpusPath);
//        buildCorpus.buildCorpus();
//
//        System.out.println(BuildCorpus.calculatePageNumber(corpusPath));
//
//        BuildCorpus b = new BuildCorpus(corpusPath, "");
//        b.buildDico();
//
//        you must execute first b.buildDico() to execute b.buildWordRelation()
        BuildCorpus b2 = new BuildCorpus(corpusPath, "");
        b2.buildWordRelation();

        // build CLI Matrix
        BuildCLI b = new BuildCLI(corpusPath, matrixCLIPath);
        b.buildCLI();

        Pagerank p = new Pagerank(matrixCLIPath);

        p.pagerank();

        // create a file mapping id to title (need that for the URL)
        BuildCorpus.pageIdToTitle(corpusPath, pageIdTitlePath);


    }




}
