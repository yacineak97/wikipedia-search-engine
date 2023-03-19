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

public class BuildCorpus {
    private int i = 0;
    private String id = "";
    private String title = "";
    private String sourceFilePath;
    private String corpusPath;
    private static String dicoFilePath = "ressources/top_words.txt";
    private static String wordPageRelationPath = "ressources/word-page-relation.txt";
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
                                i++;
                                System.out.println(i);
                            }
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

    public void buildDico() {
        try {
            FileReader fileReader = new FileReader(sourceFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);// we can use instead new FileInputStream(path.toFile() but not very powerful and its slow

            Map<String, Integer> dictionary = new TreeMap<String, Integer>();
            Set<String> uniqueWords = new HashSet<>();

            Pattern pattern = Pattern.compile("[A-Za-zàâçéèêëîïôûùüÿñæœ]{2,}"); // extract all french words  or you can use that [\p{L}\p{M}*]{2,}

            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("id")) {
                        id = reader.getElementText();
                    }

                    if (startElement.getName().getLocalPart().equals("title")) {
                        title = reader.getElementText().toLowerCase();
                    }

                    if (startElement.getName().getLocalPart().equals("text")) {
                        String text = reader.getElementText().toLowerCase();
                        Matcher matcherText = pattern.matcher(text);
                        Matcher matcherTitle = pattern.matcher(title);

                        while (matcherText.find()) {
                            uniqueWords.add(matcherText.group().toLowerCase());
                        }

                        while (matcherTitle.find()) {
                            uniqueWords.add(matcherTitle.group().toLowerCase());
                        }

                        for (String word : uniqueWords) {
                            if (!dictionary.containsKey(word)) {
                                dictionary.put(word, 1);
                            } else {
                                int i = dictionary.get(word) + 1;
                                dictionary.put(word, i);
                            }
                        }

                        uniqueWords.clear();

                        i++;
                        System.out.println(i);
                    }
                }
            }
            Comparator<String> valueComparator = new Comparator<String>() {
                public int compare(String a, String b) {
                    int compare = dictionary.get(b).compareTo(dictionary.get(a));
                    if (compare == 0) {
                        return a.compareTo(b);
                    } else {
                        return compare;
                    }
                }
            };

            TreeMap<String, Integer> sortedDictionaryByValue = new TreeMap<>(valueComparator);
            sortedDictionaryByValue.putAll(dictionary);

            BufferedWriter w = new BufferedWriter(new FileWriter(dicoFilePath));
            int count = 0;
            for (Map.Entry<String, Integer> entry : sortedDictionaryByValue.entrySet()) {
                String word = entry.getKey();
                int frequencyInCorpus = entry.getValue();
                w.write(word + ": " + frequencyInCorpus);
                w.newLine();
                count++;
                if (count == 50000) {
                    break;
                }
            }
            w.flush();
//            // Printing the TreeMap in alphabetical order
//            for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // you need create dico first to execute that
    public void buildWordRelation() {
        try {
            FileReader fileReader = new FileReader(sourceFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);// we can use instead new FileInputStream(path.toFile() but not very powerful and its slow

            FileReader dicoFile = new FileReader(dicoFilePath);
            BufferedReader b = new BufferedReader(dicoFile);
            Map<String, Map<String, Integer>> wordPageFreq = new HashMap<>();
            Map<String, Integer> pageFreqMap;
            Pattern pattern = Pattern.compile("[A-Za-zàâçéèêëîïôûùüÿñæœ]{2,}");

            String line;
            String[] dicoWords = new String[50000];
            int i = 0;
            while ((line = b.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":");
                dicoWords[i] = parts[0].trim();
                i++;
            }

//            this is so slow because of Collections.frequency
//            i=0;
//            while (reader.hasNext()) {
//                XMLEvent nextEvent = reader.nextEvent();
//                if (nextEvent.isStartElement()) {
//                    StartElement startElement = nextEvent.asStartElement();
//                    if (startElement.getName().getLocalPart().equals("id")) {
//                        id = reader.getElementText();
//                    }
//
//                    if (startElement.getName().getLocalPart().equals("title")) {
//                        title = reader.getElementText();
//                    }
//
//                    if (startElement.getName().getLocalPart().equals("text")) {
//                        String text = reader.getElementText();
//                        Matcher matcherText = pattern.matcher(text);
//                        Matcher matcherTitle = pattern.matcher(title);
//                        List<String> listOfWordsInPage = new ArrayList<String>();
//
//                        while (matcherText.find()) {
//                            listOfWordsInPage.add(matcherText.group().toLowerCase());
//                        }
//
//                        while (matcherTitle.find()) {
//                            listOfWordsInPage.add(matcherTitle.group().toLowerCase());
//                        }
//
//                        for(int j=0; j<dicoWords.length; j++){
//                            int wordCount = Collections.frequency(listOfWordsInPage, dicoWords[j]);
//                            if (wordCount != 0) {
//                                if (!wordPageFreq.containsKey(dicoWords[j])) {
//                                    pageFreqMap = new HashMap<>();
//                                    pageFreqMap.put(id, wordCount);
//                                } else {
//                                    pageFreqMap = wordPageFreq.get(dicoWords[j]);
//                                    pageFreqMap.put(id, wordCount);
//                                }
//                                wordPageFreq.put(dicoWords[j], pageFreqMap);
//                            }
//                        }
//                        i++;
//                        System.out.println(i);
//                    }
//                }
//            }


            Set<String> dicoWordsSet = new HashSet<>(Arrays.asList(dicoWords));
            StringBuilder listOfWordsInPageBuilder = new StringBuilder();

            i=0;
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("id")) {
                        id = reader.getElementText().trim();
                    }

                    if (startElement.getName().getLocalPart().equals("title")) {
                        title = reader.getElementText().toLowerCase();
                    }

                    if (startElement.getName().getLocalPart().equals("text")) {
                        String text = reader.getElementText().toLowerCase();
                        Matcher matcherText = pattern.matcher(text);
                        Matcher matcherTitle = pattern.matcher(title);

                        // Use StringBuilder instead of List
                        while (matcherText.find()) {
                            listOfWordsInPageBuilder.append(matcherText.group().toLowerCase());
                            listOfWordsInPageBuilder.append(" ");
                        }

                        while (matcherTitle.find()) {
                            listOfWordsInPageBuilder.append(matcherTitle.group().toLowerCase());
                            listOfWordsInPageBuilder.append(" ");
                        }

                        // Iterate over words in listOfWordsInPage and check if each word is in dicoWordsSet
                        String[] listOfWordsInPage = listOfWordsInPageBuilder.toString().split(" ");

                        Map<String, Integer> wordCounts = new HashMap<>();
                        for (String word : listOfWordsInPage) {
                            if (dicoWordsSet.contains(word)) {
                                int count = wordCounts.getOrDefault(word, 0);
                                wordCounts.put(word, count + 1);
                            }
                        }

                        // Iterate over wordCounts and add to wordPageFreq
                        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                            String word = entry.getKey();
                                int count = entry.getValue();
                                if (count != 0) {
                                    if (!wordPageFreq.containsKey(word)) {
                                        pageFreqMap = new HashMap<>();
                                    } else {
                                        pageFreqMap = wordPageFreq.get(word);
                                    }
                                    pageFreqMap.put(id, count);
                                    wordPageFreq.put(word, pageFreqMap);
                                }
                        }

                        i++;
                        System.out.println(i);

                        // Reset StringBuilder
                        listOfWordsInPageBuilder.setLength(0);
                    }
                }
            }

            BufferedWriter w = new BufferedWriter(new FileWriter(wordPageRelationPath));
            for (String word : wordPageFreq.keySet()) {
                w.write(word + ":");
                pageFreqMap = wordPageFreq.get(word);
                for (String page : pageFreqMap.keySet()) {
                    int frequency = pageFreqMap.get(page);
                    w.write(page + "," + frequency + ";");
                }
                w.newLine();
            }

            w.flush();
            w.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void pageIdToTitle(String srcFile, String destFile) {
        try {
            FileReader fileReader = new FileReader(srcFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);// we can use instead new FileInputStream(path.toFile() but not very powerful and its slow

            Map<Integer, String> dictionary = new TreeMap<>();
            int id = 0;
            String title = "";

            int i = 0;
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("id")) {
                        id = Integer.parseInt(reader.getElementText());
                    }

                    if (startElement.getName().getLocalPart().equals("title")) {
                        title = reader.getElementText();
                        if (dictionary.containsKey(id)) {
                            System.out.println("! Duplicate Page ID : " + id);
                        }
                        dictionary.put(id, title);
//                        System.out.println(i);
                        i++;
                    }
                }
            }

            BufferedWriter w = new BufferedWriter(new FileWriter(destFile));
            for (Map.Entry<Integer, String> entry : dictionary.entrySet()) {
                id = entry.getKey();
                title = entry.getValue();
                w.write(id + ": " + title);
                w.newLine();
            }

            w.flush();
            w.close();
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
//        text = text.toLowerCase(); // it's not good here there is page named Champ and other named CHAMP
        text = text.replaceAll("[\n]{2,}", "\n");
        text = text.trim().replaceAll("[ ]{2,}", " ");

        return text;
    }
}












