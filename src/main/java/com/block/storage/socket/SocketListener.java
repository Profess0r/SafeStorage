package com.block.storage.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SocketListener extends Thread {
    private boolean serverStopping = false;
    private ExecutorService pool;

    @Override
    public void run() {
        pool = new ThreadPoolExecutor(
                8, 64, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(256));

        try (ServerSocket listener = new ServerSocket(5555)) {
            while (!serverStopping) {
                Socket socket = listener.accept();
                RequestHandler requestHandler = new RequestHandler(socket);
                pool.submit(requestHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
