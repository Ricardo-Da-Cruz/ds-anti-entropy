package ds.entropy;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {

        InetAddress[] peers = new InetAddress[args.length];

        for (int i = 0; i < args.length; i++) {
            try {
                peers[i] = InetAddress.getByName(args[i]);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        Peer peer = new Peer(peers);


    }
}