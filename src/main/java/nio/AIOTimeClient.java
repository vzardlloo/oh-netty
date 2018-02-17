package nio;


public class AIOTimeClient {

    public static void main(String[] args) {
        int port = 9000;
        try {
            if (args != null && args.length > 0) {
                port = Integer.valueOf(args[0]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        AsyncTimeClientHandler clientHandler = new AsyncTimeClientHandler("127.0.0.1", port);
        new Thread(clientHandler, "AIO_AsyncTimeClientHandler-001").start();
    }


}
