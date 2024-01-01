package ds.entropy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import ds.poisson.PoissonProcess;

public class Peer implements Runnable{

    private Socket[] addresses;

    private String[] words = new String[10000];

    private ArrayList<String> state = new ArrayList<>();

    Peer(InetAddress[] addresses) {
        this.addresses = addresses;

        try {
            Scanner file = new Scanner(new File("words.txt"));
            int numWords = 0;
            while (file.hasNext()) {
                words[numWords] = file.nextLine();
                numWords++;
            }
            System.out.println("Loaded " + numWords + " words.");

            ServerSocket serverSocket = new ServerSocket(5000);

            serverSocket.setSoTimeout(1000);

            LinkedList<Socket> sockets = new LinkedList<>();

            while (sockets.size() <= addresses.length) {
                try {
                    Socket socket = serverSocket.accept();
                    if (socket != null){
                        sockets.add(socket);
                    }



                } catch (IOException e) {
                    // Do nothing
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void run() {

        while (true){
            PoissonProcess pp = new PoissonProcess(4, new Random((int) (Math.random() * 1000)));
            InetAddress Peer = addresses[(int) (Math.random() * addresses.length)];


            int randomPeerIndex = (int) (Math.random() * addresses.length);
            InetAddress randomPeer = addresses[randomPeerIndex];



            System.out.println("Sending " + randomWord + " to " + randomPeer);

        }
    }

}


private Class Server {


}
