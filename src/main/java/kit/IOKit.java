package kit;


import java.io.Closeable;
import java.io.IOException;

public class IOKit {

    public static void close(Closeable... closeables) {
        for (Closeable c : closeables) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
