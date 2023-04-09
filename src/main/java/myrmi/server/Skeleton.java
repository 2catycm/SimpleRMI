package myrmi.server;

import myrmi.Remote;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Skeleton extends Thread {
    static final int BACKLOG = 5;
    private Remote remoteObj;

    private String host;
    private int port;
    private int objectKey;

    public int getPort() {
        return port;
    }

    public Skeleton(Remote remoteObj, RemoteObjectRef ref) {
        this(remoteObj, ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    public Skeleton(Remote remoteObj, String host, int port, int objectKey) {
        super();
        this.remoteObj = remoteObj;
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        this.setDaemon(false);
    }

    @Override
    public void run() {
//        /*TODO: implement method here
//         * You need to:
//         * 1. create a server socket to listen for incoming connections
//         * 2. use a handler thread to process each request (use SkeletonReqHandler)
//         *  */
//
//        throw new NotImplementedException();
        try (ServerSocket serverSocket = new ServerSocket(this.port, BACKLOG)){
            while(!this.isInterrupted()){
                final Socket accept = serverSocket.accept();
                new SkeletonReqHandler(accept, this.remoteObj, this.objectKey).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
