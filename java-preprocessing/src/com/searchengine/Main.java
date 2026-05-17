package com.searchengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    // Base resources folder
    static final Path BASE_RESOURCES = Paths.get("resources");

    // Raw wikipedia dumps
    static String sourceFilePath = BASE_RESOURCES.resolve("raw/frwiki-20230320-pages-articles.xml").toString();
    static String sourceFileSmallPath = BASE_RESOURCES.resolve("raw/frwiki10000.xml").toString();
    static String testInput = BASE_RESOURCES.resolve("raw/input.xml").toString();

    // Java generated processed files
    static String corpusPath = BASE_RESOURCES.resolve("java-processed/corpus.xml").toString();
    static String matrixCLIPath = BASE_RESOURCES.resolve("java-processed/CLI.txt").toString();
    static String matrixCLIPathTest = BASE_RESOURCES.resolve("java-processed/CLI2.txt").toString();
    static String pageIdTitlePath = BASE_RESOURCES.resolve("java-processed/pageid_title.txt").toString();

    public static void main(String[] args) {

        try {
            // Create java-processed directory if it doesn't exist
            Files.createDirectories(BASE_RESOURCES.resolve("java-processed"));

        } catch (IOException e) {
            System.err.println("Failed to create directories: " + e.getMessage());
            return;
        }

        BuildCorpus buildCorpus = new BuildCorpus(sourceFileSmallPath, corpusPath);
        buildCorpus.buildCorpus();

        // This is just to calculate the number of pages in the corpus
        // System.out.println(BuildCorpus.calculatePageNumber(corpusPath));
        BuildCorpus b = new BuildCorpus(corpusPath, "");
        b.buildDico();

        // You must execute first b.buildDico() to execute b.buildWordRelation()
        BuildCorpus b2 = new BuildCorpus(corpusPath, "");
        b2.buildWordRelation();

        // build CLI Matrix
        BuildCLI b3 = new BuildCLI(corpusPath, matrixCLIPath);
        b3.buildCLI();

        Pagerank p = new Pagerank(matrixCLIPath);
        p.pagerank();

        // // create a file mapping id to title (need that for the URL)
        BuildCorpus.pageIdToTitle(corpusPath, pageIdTitlePath);
    }
}
