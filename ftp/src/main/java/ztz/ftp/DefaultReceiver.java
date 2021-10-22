package ztz.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author zhoutzzz
 */
public class DefaultReceiver implements DataReceiver{
    @Override
    public void receive(String name, InputStream data) throws IOException {
        System.out.println("receiving file: [" + name + "]");
        System.out.println("receiving data:");
        PrintStream out = System.out;
        byte[] b = new byte[1024];
        while (data.read(b) != -1) {
            out.write(b);
        }
        System.out.println("");
    }
}
