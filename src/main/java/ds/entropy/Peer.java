package ds.entropy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.*;

import ds.poisson.PoissonProcess;

public class Peer implements Runnable{

    private final InetAddress[] addresses;

    private final ArrayList<String> words;

    private final Map<InetAddress,Integer> indexes;

    private final ServerSocket server;

    class Server implements Runnable{

        @Override
        public void run(){

            while (true){
                try {
                    Socket client = server.accept();
                    updateValues(client, indexes.get(client.getInetAddress()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    Peer(InetAddress[] addresses) throws IOException {
        server = new ServerSocket(5000);
        this.addresses = addresses;
        words = new ArrayList<>();

        this.indexes = new HashMap<>();
        for (InetAddress address : addresses) {
            indexes.put(address, 0);
        }

        new Thread(new WordGenerator(words)).start();
        new Thread(new Server()).start();
    }

    public void run() {
        // given that the network topology that was given has only one possible path between any two peers It simplifies
        // the logic since any update will not have information that was already received from another peer.

        while (true){
            PoissonProcess pp = new PoissonProcess(4, new Random((int) (Math.random() * 1000)));
            double t = pp.timeForNextEvent() * 60.0 * 1000.0;

            try {
                Thread.sleep((int) t);
                int randomPeer = (int) (Math.random() * addresses.length);

                Socket socket = new Socket(addresses[randomPeer], 5000);
                updateValues(socket, indexes.get(addresses[randomPeer]));

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateValues(Socket client, int index) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        // should the synchronized block include all this code? no words can be added while this is running
        synchronized (words){
            if (index < words.size()) {
                String message = String.join(",", words.subList(index, words.size()));

                System.out.println("sending message:\n\t" + message);
                System.out.println("\tto: " + client.getInetAddress() + "at" + Instant.now() + "\n");

                out.println(message);
            }else{
                out.println();
                System.out.println("no new words to send to " + client.getInetAddress());
            }

            String message = in.readLine();
            if (message == null || message.equals("END")){
                System.out.println("closing connection to " + client.getInetAddress());
                client.close();
                throw new RuntimeException("connection closed");
            }

            System.out.println("received message:\n\t" + message);
            System.out.println("\tfrom: " + client.getInetAddress() + "at" + Instant.now() + "\n");

            words.addAll(Arrays.asList(message.split(",")));
            indexes.put(client.getInetAddress(), words.size());

        }



    }

}