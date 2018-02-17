package example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) {
        int port = 9999;
        ExecutorService executorService = Executors.newCachedThreadPool();
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        } else {
            //use default value
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("The server is listen at : " + port);
            Socket socket = null;
            while (true) {
                try {
                    socket = serverSocket.accept();
                    //System.out.println(socket.getRemoteSocketAddress()+" connect!");
                    executorService.execute(new RequestHandler(socket));


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

class RequestHandler implements Runnable {
    private Socket socket = null;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        long start = 0L;
        long end = 0L;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String inputLine = null;
            start = System.currentTimeMillis();
            // be serious,there may be make a bug
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
            end = System.currentTimeMillis();
            System.out.println("spend : " + (end - start) + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
