package nio;


public class NIOTimeClient {

    public static void main(String[] args) {
        int port = 9090;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (Exception e) {
                //ignore
            }
        }
        new Thread(new NIOTimeClientHandle("127.0.0.1", port)).start();
    }
}
