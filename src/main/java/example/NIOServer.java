package example;


import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    public static void main(String[] args) {
        int port = 9999;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        } else {
            // use default
        }
        new Thread(new NIORequestHandler(port)).start();
    }

}


class NIORequestHandler implements Runnable {

    Selector selector = null;

    ServerSocketChannel serverSocketChannel = null;

    public NIORequestHandler(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;

                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            SocketChannel sc = ssc.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        }
                        if (key.isReadable()) {
                            long start = System.currentTimeMillis();
                            SocketChannel sc = (SocketChannel) key.channel();
                            //1024 at there is enough
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            int readByte = sc.read(readBuffer);
                            if (readByte > 0) {
                                readBuffer.flip();
                                byte[] bytes = new byte[readBuffer.remaining()];
                                readBuffer.get(bytes);
                                ByteBuffer wirteBuffer = ByteBuffer.allocate(1024);
                                byte[] newBytes = ("re:" + new String(bytes, "UTF-8")).getBytes();
                                wirteBuffer.put(newBytes);
                                wirteBuffer.flip();
                                sc.write(wirteBuffer);
                                long end = System.currentTimeMillis();
                                System.out.println("spend : " + (end - start) + " ms");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}