package ds.entropy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        InetAddress[] peers = new InetAddress[args.length];

        try {
            for (int i = 0; i < args.length; i++) {
                peers[i] = InetAddress.getByName(args[i]);
            }
            new Thread(new Peer(peers)).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}