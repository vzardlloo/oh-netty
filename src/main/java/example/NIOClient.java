package example;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.LockSupport;

public class NIOClient {

    public static void main(String[] args) {
        int port = 9999;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        } else {
            // use default value
        }

        for (int i = 0; i < 100; i++) {
            new Thread(new NIORequest(port), "thread-" + i).start();
        }
    }


}


class NIORequest implements Runnable {

    private Selector selector = null;
    private SocketChannel socketChannel = null;
    int port;

    public NIORequest(int port) {
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        inputHandle(key);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        //IOKit.close(socketChannel);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void doConnect() throws IOException {
        if (socketChannel.connect(new InetSocketAddress("127.0.0.1", port))) {
            System.out.println("连接建立成功...");
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            System.out.println("等待建立连接...");
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        LockSupport.parkNanos(1000 * 1000 * 1000);
        byte[] msg = "hello".getBytes();
        LockSupport.parkNanos(1000 * 1000 * 1000);
        ByteBuffer buffer = ByteBuffer.allocate(msg.length);
        buffer.put(msg);
        buffer.flip();
        sc.write(buffer);
    }

    private void inputHandle(SelectionKey key) throws IOException {
        if (key.isValid()) {
            SocketChannel sc = (SocketChannel) key.channel();
            sc.configureBlocking(false);
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                }
            }
            if (key.isReadable()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readByte = sc.read(readBuffer);
                if (readByte > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String msg = new String(bytes, "UTF-8");
                    System.out.println(msg);
                }
            }
        }
    }
}
