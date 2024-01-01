package ds.entropy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import ds.poisson.PoissonProcess;

public class Peer implements Runnable{

    private final InetAddress[] addresses;

    private final Map<InetAddress,ArrayList<String>> words;

    private final Map<InetAddress,Map<InetAddress, Integer>> indexes;

    private final ServerSocket server;

    class Server implements Runnable{

        @Override
        public void run(){

            while (true){
                try {
                    Socket client = server.accept();
                    updateValues(client, addresses, words, indexes.get(client.getInetAddress()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    Peer(InetAddress[] addresses) throws IOException {
        server = new ServerSocket(5000);
        this.addresses = addresses;

        this.indexes = new HashMap<>();
        for (InetAddress inetAddress : addresses) {
            indexes.put(inetAddress, new HashMap<>());
            for (InetAddress address : addresses) {
                indexes.get(inetAddress).put(address, 0);
            }
        }

        InetAddress localHost = InetAddress.getLocalHost();

        words = new HashMap<>();
        words.put(localHost, new ArrayList<>());
        for (InetAddress address : addresses) {
            words.put(address, new ArrayList<>());
        }

        new Thread(new WordGenerator(words.get(localHost))).start();
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
                updateValues(socket, addresses, words, indexes.get(addresses[randomPeer]));

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void updateValues(Socket client, InetAddress[] addresses, Map<InetAddress, ArrayList<String>> words, Map<InetAddress, Integer> indexes) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        for (InetAddress address : addresses) {
            synchronized (words.get(address)){
                if (indexes.get(address) < words.get(address).size()) {
                    String message =
                            address.toString() +
                                    ":" +
                                    String.join(",", words.get(address).subList(indexes.get(address), words.get(address).size()));

                    System.out.println("sending message:\n\t" + message);
                    System.out.println("to: " + client.getInetAddress() + "\n");

                    out.println(message);
                }else {
                    System.out.println("no new words to send to " + client.getInetAddress() + "from " + address.toString());
                    out.println(address + ":");
                }

                indexes.put(address, words.get(address).size());
            }
        }
        out.println("END");
        out.flush();

        while (true){
            String message = in.readLine();
            if (message == null || message.equals("END")){
                break;
            }
            System.out.println("received message:\n\t" + message);
            String[] split = message.split(":");

            InetAddress address = InetAddress.getByName(split[0]);
            String[] newWords = split[1].split(",");

            synchronized (words.get(address)) {
                words.get(address).addAll(Arrays.asList(newWords));
                indexes.put(address, words.get(address).size());
            }
        }
    }

}