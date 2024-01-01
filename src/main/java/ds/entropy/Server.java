package ds.entropy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static ds.entropy.Peer.updateValues;

public class Server implements Runnable{

    private final InetAddress[] addresses;

    private final Map<InetAddress, ArrayList<String>> words;

    private final Map<InetAddress,Integer> indexes;

    private final ServerSocket server;

    public Server(InetAddress[] addresses, Map<InetAddress, ArrayList<String>> words, Map<InetAddress, Integer> indexes) {
        try {
            server = new ServerSocket(5000);
            this.addresses = addresses;
            this.words = words;
            this.indexes = indexes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run(){

        while (true){
            try {
                Socket client = server.accept();
                updateValues(client, addresses, words, indexes);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
