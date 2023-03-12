package com.searchengine.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Test {
    public static void main(String[] args) {
        long i = 0;
        String id = "";
        String title = "";
        boolean insideRevision = false;
        try {
            Path path = Paths.get("ressources/input.xml");
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(path.toFile()));

            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("text")) {
                        String text = reader.getElementText();
                        text = text.replaceAll("(?s)\\{\\{.*?\\}\\}", "");
                        text = text.replaceAll("\\[\\[[^\\]]*?:.*?\\]\\]", "");
                        text = text.replaceAll("(?s)\\<.*?\\>", ""); // dot all (?s)
                        text = text.replaceAll("(?s)==\\s*Notes et références\\s*==.*", "");
                        text = text.replaceAll("(?s)==\\s*Voir aussi\\s*==.*", "");
                        text = text.replaceAll("(?s)==\\s*Bibliographie\\s*==.*", "");
                        text = text.replaceAll("(?s)==\\s*Liens externes\\s*==.*", "");
                        text = text.replaceAll("(?s)==\\s*Articles connexes\\s*==.*", "");
                        text = text.replaceAll("(?s)==\\s*Sources\\s*==.*", "");
                        text = text.replaceAll("(?s)==\\s*Références\\s*==.*", "");
//                        text = text.replaceAll("[^\s]*'", ""); // j' l'
                        text = text.toLowerCase();

                        if (countWordsUsingStringTokenizer(text) < 1000) {

                        }
                        System.out.println(text);
                    }
                }
            }

//            System.out.println(Pattern.matches("(\\w\\d)\\1", "a2a2")); //true
//            System.out.println(Pattern.matches("(\\w\\d)\\1", "a2b2")); //false
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int countWordsUsingStringTokenizer(String sentence) {
        if (sentence == null || sentence.isEmpty()) { return 0; }
        StringTokenizer tokens = new StringTokenizer(sentence); return tokens.countTokens();
    }
}
