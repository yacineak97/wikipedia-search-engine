package com.searchengine.test;

//import edu.stanford.nlp.coref.data.CorefChain;
//import edu.stanford.nlp.ling.*;
//import edu.stanford.nlp.ie.util.*;
//import edu.stanford.nlp.pipeline.*;
//import edu.stanford.nlp.semgraph.*;
//import edu.stanford.nlp.trees.*;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//
//import org.tartarus.snowball.SnowballStemmer;
//import org.tartarus.snowball.ext.frenchStemmer;


//public class TestLemma {
//
//    public static String text = "Il est parti en vacances à Paris en été.";
//
//    public static void main(String[] args) {
//        // set up pipeline properties
//        Properties props = new Properties();
//        // set the list of annotators to run
//        props.setProperty("tokenize.language", "fr");
//        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/french-ud.tagger");
//        props.setProperty("mwt.mappingFile", "edu/stanford/nlp/models/mwt/french/french-mwt.tsv");
//        props.setProperty("mwt.pos.model", "edu/stanford/nlp/models/mwt/french/french-mwt.tagger");
//        props.setProperty("mwt.statisticalMappingFile", "edu/stanford/nlp/models/mwt/french/french-mwt-statistical.tsv");
//        props.setProperty("mwt.preserveCasing", "false");
//        props.setProperty("ner.model", "edu/stanford/nlp/models/ner/french-wikiner-4class.crf.ser.gz");
//        props.setProperty("ner.applyFineGrained", "false");
//        props.setProperty("ner.applyNumericClassifiers", "false");
//        props.setProperty("ner.useSUTime", "false");
//        props.setProperty("parse.model", "edu/stanford/nlp/models/srparser/frenchSR.beam.ser.gz");
//        props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/UD_French.gz");
//
//        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
//        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
////        props.setProperty("coref.algorithm", "neural");
//        // build pipeline
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//        // create a document object
//        CoreDocument document = new CoreDocument(text);
//        pipeline.annotate(document);
//        List<CoreLabel> coreLabelList = document.tokens();
//
//        for(CoreLabel coreLabel : coreLabelList) {
//            String lemma = coreLabel.lemma();
//            System.out.println(lemma);
//        }


//        BufferedReader br = null;
//
//        try {
//
//            String sCurrentLine;
//            br = new BufferedReader(new FileReader("src/com/searchengine/text.txt"));
//            SnowballStemmer stemmer = (SnowballStemmer) new frenchStemmer();
//
//            while ((sCurrentLine = br.readLine()) != null) {
//
//                String[] tokens = sCurrentLine.split(" ");
//                for (String string : tokens) {
//                    stemmer.setCurrent(string);
//                    stemmer.stem();
//                    String stemmed = stemmer.getCurrent();
//
//                    System.out.println(stemmed);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (br != null)br.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//}