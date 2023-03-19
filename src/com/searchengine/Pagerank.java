package com.searchengine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Pagerank {
    String matrixCLIPath;
    String pagerankFile = "ressources/pagerank-scores.txt";
    public Pagerank(String matrixCLIPath) {
        this.matrixCLIPath = matrixCLIPath;
    }

    public void pagerank() {
        int k = 50;
        int totalNumberOfPages = 94374;
        try {
            FileReader fileReader = new FileReader(matrixCLIPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            ArrayList<Float> C = new ArrayList<Float>();
            ArrayList<Long> L = new ArrayList<Long>();
            ArrayList<Long> I = new ArrayList<Long>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String whichOne = line.split(":")[0];
                String values = line.split(":")[1];
                String[] valuesArray = values.split(",");
                if (whichOne.equals("C")) {
                    for(int i=0; i<valuesArray.length; i++){
                        C.add(Float.valueOf(valuesArray[i]));
                    }
                }

                if (whichOne.equals("L")) {
                    for(int i=0; i<valuesArray.length; i++){
                        L.add(Long.valueOf(valuesArray[i]));
                    }
                }

                if (whichOne.equals("I")) {
                    for(int i=0; i<valuesArray.length; i++){
                        I.add(Long.valueOf(valuesArray[i]));
                    }
                }
            }

            ArrayList<Float> proba = new ArrayList<Float>();

            // initialize proba ArrayList
            for (int i = 0; i < totalNumberOfPages; i++) {
                proba.add(1f/4);
            }

            for (int i = 0; i < k; i++) {
                proba = matrixVectorProduct(C, L, I, proba);
            }

            BufferedWriter w = new BufferedWriter(new FileWriter(pagerankFile));
            for (int i = 0; i < proba.size(); i++) {
                w.write(i + ":" +proba.get(i).toString() + "\n");
            }

            w.flush();
            w.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<Float> matrixVectorProduct(ArrayList<Float> C, ArrayList<Long> L, ArrayList<Long> I, ArrayList<Float> proba) {
        ArrayList<Float> product = new ArrayList<Float>();
        int totalNumberOfPages = proba.size();
        float emptyLinesSum = 0;
        float alpha = 1f/7;

        // initialize product ArrayList
        for (int i = 0; i < proba.size(); i++) {
            product.add(0f);
        }

        for(int i=0;i<totalNumberOfPages;i++) {
            if (L.get(i) == L.get(i+1)) {
                emptyLinesSum += proba.get(i);
            }

            for(long j=L.get(i);j<L.get(i+1);j++){
                float newValue = product.get(Math.toIntExact(I.get((int) j))) + C.get((int) j)*proba.get(i);
                product.set(Math.toIntExact(I.get((int) j)), newValue);
            }
        }

        emptyLinesSum = emptyLinesSum/totalNumberOfPages;

        for(int i=0;i<totalNumberOfPages;i++) {
            float oldValue = product.get(i);
            float newValue = (1-alpha)*(oldValue + emptyLinesSum) + alpha/totalNumberOfPages;

            product.set(i, newValue);
        }

        return product;
    }
}
