package com.searchengine;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildCLI {
    private String corpusPath;
    private String matrixCLIPath;
    public BuildCLI(String corpusPath, String matrixCLIPath) {
        this.corpusPath = corpusPath;
        this.matrixCLIPath = matrixCLIPath;
    }

    public void buildCLI() {
        Map<String, Long> pagesIds = assignPagesIDs();

        try {
            FileReader fileReader = new FileReader(corpusPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);

            String pattern = "\\[\\[([^\\|\\]]+)(\\|([^\\]]+))?\\]\\]";
            Pattern regex = Pattern.compile(pattern);
            ArrayList<Float> C = new ArrayList<Float>();
            ArrayList<Long> L = new ArrayList<Long>();
            ArrayList<Long> I = new ArrayList<Long>();

            L.add(0L);
            long dPlus;
            String title = "";
            int j = 0;
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("title")) {
                        title = reader.getElementText().trim();
                        j++;
                        System.out.println(j);
                    }

                    if (startElement.getName().getLocalPart().equals("text")) {
                        String text = reader.getElementText();
                        Matcher matcher = regex.matcher(text);

                        dPlus = 0;
                        int LastSizeC = C.size();
                        ArrayList<Long> outGoingPages = new ArrayList<>();
                        while (matcher.find()) {
                            String match = matcher.group(1).trim();
                            if (pagesIds.get(match) != null) {
                                C.add(1F);
                                outGoingPages.add(pagesIds.get(match));
                                dPlus++;
                            }

                        }

                        Collections.sort(outGoingPages);

                        for (int i = 0; i < outGoingPages.size(); i++) {
                            I.add(outGoingPages.get(i));
                        }

                        L.add(L.get(Math.toIntExact(pagesIds.get(title))) + dPlus);
                        for (int i = LastSizeC; i < LastSizeC + dPlus; i++) {
                            float value = C.get(i);
                            C.set(i, (value / dPlus));
                        }
                    }
                }
            }

            BufferedWriter w = new BufferedWriter(new FileWriter(matrixCLIPath));

            w.write("C:");
            for(int i=0;i<C.size();i++) {
                w.write(String.valueOf(C.get(i))+",");
            }
            w.newLine();

            w.write("L:");
            for(int i=0;i<L.size();i++) {
                w.write(String.valueOf(L.get(i))+",");
            }
            w.newLine();

            w.write("I:");
            for(int i=0;i<I.size();i++) {
                w.write(String.valueOf(I.get(i))+",");
            }

            w.flush();
            w.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, Long> assignPagesIDs() {
        try {
            FileReader fileReader = new FileReader(corpusPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlInputFactory.setProperty("jdk.xml.totalEntitySizeLimit", "0");
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(bufferedReader);

            Map<String, Long> pageIDs = new HashMap<>();
            String title;
            long id = 0;
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("title")) {
                        title = reader.getElementText().trim();
                        pageIDs.put(title, id);
                        id++;
                        System.out.println(id);
                    }
                }
            }

//            for (Map.Entry<String, Long> entry : pageIDs.entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }

            return pageIDs;
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
