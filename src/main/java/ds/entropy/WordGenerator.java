package ds.entropy;

import ds.poisson.PoissonProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class WordGenerator implements Runnable{

    private String[] words = new String[10000];

    private ArrayList<String> list = new ArrayList<>();

    WordGenerator(ArrayList<String> list) {
        try {
            this.list = list;
            Scanner file = new Scanner(new File("words.txt"));
            int numWords = 0;
            while (file.hasNext()) {
                words[numWords] = file.nextLine();
                numWords++;
            }
            System.out.println("Loaded " + numWords + " words.");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        PoissonProcess pp = new PoissonProcess(4, new Random((int) (Math.random() * 1000)));
        while (true) {
            double t = pp.timeForNextEvent() * 60.0 * 1000.0;

            try{
                Thread.sleep((int)t);
                list.add(words[(int) (Math.random() * words.length)]);
            } catch (InterruptedException e) {
                System.out.println("thread interrupted");
                e.printStackTrace(System.out);
            }
        }
    }
}
