import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;
import ztz.ftp.CrlfStringDecoder;
import ztz.ftp.DefaultReceiver;
import ztz.ftp.FtpServerHandler;
import ztz.ftp.cmd.DefaultCommandExecutionTemplate;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * User: alexkasko
 * Date: 12/28/12
 */
public class FtpServerTest {

    @Test
    public void test() throws IOException, InterruptedException {
        final DefaultCommandExecutionTemplate defaultCommandExecutionTemplate = new DefaultCommandExecutionTemplate(new DefaultReceiver());
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipe = ch.pipeline();
                        pipe.addLast("decoder", new CrlfStringDecoder());
                        pipe.addLast("handler", new FtpServerHandler(defaultCommandExecutionTemplate));
                    }

                });
        b.localAddress(2121).bind();
        FTPClient client = new FTPClient();
//        https://issues.apache.org/jira/browse/NET-493

        client.setBufferSize(0);
        client.connect("127.0.0.1", 2121);
        assertEquals(230, client.user("anonymous"));

        // active
        assertTrue(client.setFileType(FTP.BINARY_FILE_TYPE));
        assertEquals("/", client.printWorkingDirectory());
        assertTrue(client.changeWorkingDirectory("/foo"));
        assertEquals("/foo", client.printWorkingDirectory());
        assertTrue(client.listFiles("/foo").length == 0);
        assertTrue(client.storeFile("bar", new ByteArrayInputStream("content".getBytes())));
        assertTrue(client.rename("bar", "baz"));
        //  assertTrue(client.deleteFile("baz"));

        // passive
        assertTrue(client.setFileType(FTP.BINARY_FILE_TYPE));
        client.enterLocalPassiveMode();
        assertEquals("/foo", client.printWorkingDirectory());
        assertTrue(client.changeWorkingDirectory("/foo"));
        assertEquals("/foo", client.printWorkingDirectory());

        //TODO make a virtual filesystem that would work with directory
        //assertTrue(client.listFiles("/foo").length==1);

        assertTrue(client.storeFile("bar", new ByteArrayInputStream("content".getBytes())));
        assertTrue(client.rename("bar", "baz"));
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        assertTrue(client.retrieveFile("xx", byteOut));
        System.out.println(byteOut.toString());
        // client.deleteFile("baz");

        assertEquals(221, client.quit());
        try {
            client.noop();
            fail("Should throw exception");
        } catch (IOException e) {
            //expected;
        }

    }

    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 9999);
//            s.connect(add);
            OutputStreamWriter writer = new OutputStreamWriter(s.getOutputStream());
            BufferedWriter buf = new BufferedWriter(writer);
            buf.write("hello\n");
            buf.flush();
            InputStreamReader reader = new InputStreamReader(s.getInputStream());
            BufferedReader buf1 = new BufferedReader(reader);
            System.out.println(buf1.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Ts {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            Socket accept = serverSocket.accept();
            InputStreamReader reader = new InputStreamReader(accept.getInputStream());
            BufferedReader bufr = new BufferedReader(reader);
            System.out.println(bufr.readLine());
            OutputStreamWriter writer = new OutputStreamWriter(accept.getOutputStream());
            BufferedWriter buf = new BufferedWriter(writer);
            buf.write("lalalala");
            buf.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}