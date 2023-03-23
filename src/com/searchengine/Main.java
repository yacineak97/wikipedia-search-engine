package com.searchengine;

public class Main {
    static String sourceFilePath = "ressources/frwiki-20230301-pages-articles.xml";
    static String sourceFilePathShort = "ressources/frwiki10000.xml";
    static String testInput = "ressources/input.xml";
    static String corpusPath = "ressources/corpus.xml";
    static String matrixCLIPath = "ressources/CLI.txt";
    static String matrixCLIPathTest = "ressources/CLI2.txt";
    static String pageIdTitlePath = "ressources/pageid_title.txt";
    public static void main(String[] args) {


        BuildCorpus buildCorpus = new BuildCorpus(sourceFilePath, corpusPath);
        buildCorpus.buildCorpus();

        // This is just to calculate the number of pages in the corpus
//        System.out.println(BuildCorpus.calculatePageNumber(corpusPath));

        BuildCorpus b = new BuildCorpus(corpusPath, "");
        b.buildDico();

//        you must execute first b.buildDico() to execute b.buildWordRelation()
        BuildCorpus b2 = new BuildCorpus(corpusPath, "");
        b2.buildWordRelation();

        // build CLI Matrix
        BuildCLI b3 = new BuildCLI(corpusPath, matrixCLIPath);
        b3.buildCLI();

        Pagerank p = new Pagerank(matrixCLIPath);
        p.pagerank();

        // create a file mapping id to title (need that for the URL)
        BuildCorpus.pageIdToTitle(corpusPath, pageIdTitlePath);
    }
}
