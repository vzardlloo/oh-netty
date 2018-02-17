package example;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.locks.LockSupport;

public class BIOClient {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(new Request(), "thread-" + i).start();
        }
    }

}


class Request implements Runnable {
    @Override
    public void run() {
        Socket client = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        int sleepTime = 1000 * 1000 * 1000;
        try {
            client = new Socket();
            client.connect(new InetSocketAddress("127.0.0.1", 9999));
            writer = new PrintWriter(client.getOutputStream(), true);
            writer.print("H");
            LockSupport.parkNanos(sleepTime);
            writer.print("e");
            LockSupport.parkNanos(sleepTime);
            writer.print("l");
            LockSupport.parkNanos(sleepTime);
            writer.print("l");
            LockSupport.parkNanos(sleepTime);
            writer.print("e");
            LockSupport.parkNanos(sleepTime);
            writer.println();
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String readLine = reader.readLine();
            System.out.println("from server: " + readLine);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
                writer.close();
                reader.close();
                client = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
