package com.searchengine;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildCorpus {
    private int i = 0;
    private String id = "";
    private String title = "";
    private String sourceFilePath;
    private String corpusPath;
    public BuildCorpus(String sourceFilePath, String corpusPath) {
        this.sourceFilePath = sourceFilePath;
        this.corpusPath = corpusPath;
    }

    public void buildCorpus() {
        try {
//            System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
//            Path path = Paths.get("ressources/frwiki-20230301-pages-articles.xml");
//            Path path = Paths.get("ressources/frwiki10000.xml");

            // very powerful to use BufferedReader and very performant
            FileReader fileReader = new FileReader(sourceFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);// we can use instead new FileInputStream(path.toFile() but not very powerful and its slow

            // very powerful to use BufferedWriter and very performant
            FileWriter fileWriter = new FileWriter(corpusPath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(bufferedWriter); // we can use instead new FileOutputStream("ressources/corpus.xml") but not very powerful and its slow

            writer.writeStartElement("mediawiki");
            writer.writeAttribute("xmlns",  "http://www.mediawiki.org/xml/export-0.10/");
            writer.writeAttribute("xmlns", "", "xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi", "", "schemaLocation", "http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd");
            writer.writeAttribute("version","0.10");
            writer.writeAttribute("xml", "", "lang", "fr");

            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("id")) {
                        id = reader.getElementText();
                    }

                    if (startElement.getName().getLocalPart().equals("title")) {
                        title = reader.getElementText();
                    }

                    if (startElement.getName().getLocalPart().equals("text")) {
                        String text = reader.getElementText();
                        if (text.toLowerCase().contains("science")) {
                            text = cleanText(text);
                            if (!(countWordsUsingStringTokenizer(text) < 1000)) {
                                writer.writeStartElement("page");
                                    writer.writeStartElement("id");
                                        writer.writeCharacters(id);
                                    writer.writeEndElement();
                                    writer.writeStartElement("title");
                                        writer.writeCharacters(title);
                                    writer.writeEndElement();
                                    writer.writeStartElement("text");
                                        writer.writeCharacters(text);
                                    writer.writeEndElement();
                                writer.writeEndElement();
                            }
                            i++;
                            System.out.println(i);
                        }
                    }
                }
            }
            writer.writeEndElement();
            // we have to add this or some data will remain in the buffer and will not be written to the flush or close it will force the writer to clear the buffer to the file
            writer.flush();
            writer.close();
            bufferedWriter.close();
            fileWriter.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int calculatePageNumber(String filePath) {
        int i = 0;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);

            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                        if (startElement.getName().getLocalPart().equals("page")) {
                            i++;
                            System.out.println(i);
                        }
                    }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return i;
    }

    private String deleteNestedWithRecursionRegex(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
//        System.out.println(m.find()); // if you call find() 2 times it will start from the last find and try to find the next its like next()
        if (m.find()) {
            text = text.replaceAll(pattern, "");
            text = deleteNestedWithRecursionRegex(text, pattern);
        }

        return text;
    }

    private int countWordsUsingStringTokenizer(String sentence) {
        if (sentence == null || sentence.isEmpty()) { return 0; }
        StringTokenizer tokens = new StringTokenizer(sentence); return tokens.countTokens();
    }

    private String cleanText(String text) {
//        text = text.replaceAll("(?s)\\{\\{[^\\}]*?\\{\\{.*?\\}\\}.*?\\}\\}", "");
//        text = deleteNestedWithRecursionRegex(text, "(?s)\\{\\{[^\\{]*?\\}\\}"); // not good
        text = deleteNestedWithRecursionRegex(text, "(?s)\\{\\{((?!\\{\\{|\\}\\}).)*\\}\\}"); // not good : "(?s)\{\{.*?(?!\{\{|\}\}).*?\}\}"
        text = text.replaceAll("\\[\\[[^\\]]*?:.*?\\]\\]", "");
        text = text.replaceAll("(?s)\\<.*?\\>", ""); // dot all (?s)
        text = text.replaceAll("(?s)==\\s*Notes et références\\s*==.*", "");
        text = text.replaceAll("(?s)==\\s*Voir aussi\\s*==.*", "");
        text = text.replaceAll("(?s)==\\s*Bibliographie\\s*==.*", "");
        text = text.replaceAll("(?s)==\\s*Liens externes\\s*==.*", "");
        text = text.replaceAll("(?s)==\\s*Articles connexes\\s*==.*", "");
        text = text.replaceAll("(?s)==\\s*Sources\\s*==.*", "");
        text = text.replaceAll("(?s)==\\s*Références\\s*==.*", "");
//        text = text.replaceAll("[^\s]*'", ""); // j' l'
        text = text.toLowerCase();
        text = text.replaceAll("[\n]{2,}", "\n");
        text = text.trim().replaceAll("[ ]{2,}", " ");

        return text;
    }
}
