package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.ActivePassiveSocketManager;
import ztz.ftp.cmd.FTPAttrKeys;
import ztz.ftp.cmd.FTPCommand;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author zhoutzzz
 */
public class RetrCmd extends AbstractFTPCommand {

    private final ActivePassiveSocketManager passiveSocketManager;

    public RetrCmd(ActivePassiveSocketManager passiveSocketManager) {
        super("RETR");
        this.passiveSocketManager = passiveSocketManager;
    }

    @Override
    public void execute(ChannelHandlerContext ctx, String args) {
        FTPCommand ftpCommand = ctx.channel().attr(FTPAttrKeys.LAST_COMMAND).get();
        Socket socket;
        FileInputStream fileInputStream;
        if ("PORT".equals(ftpCommand.getCmd())) {
            socket = passiveSocketManager.getActiveSocket(ctx);
            try {
                fileInputStream = findFile(args, ctx);
                send("150 Opening binary mode data connection for " + args, ctx, args);
                sendData(fileInputStream, socket);
                send("226 Transfer complete for RETR " + args, ctx, args);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                passiveSocketManager.closeActiveSocket(ctx);
            }
        } else if ("PASV".equals(ftpCommand.getCmd())) {
            try {
                ServerSocket serverSocket = passiveSocketManager.getPassiveSocket(ctx);
                socket = serverSocket.accept();
//                fileInputStream = findFile("/Users/zhoutzzz/IdeaProjects/http-ftp-server/gradle/wrapper/gradle-wrapper.properties", ctx);
                fileInputStream = findFile(args, ctx);
                send("150 Opening binary mode data connection for " + args, ctx, args);
                sendData(fileInputStream, socket);
                send("226 Transfer complete for RETR " + args, ctx, args);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                passiveSocketManager.closePassiveSocket(ctx);
            }
        } else {
            send("503 Bad sequence of commands", ctx, args);
        }
    }

    private FileInputStream findFile(String fileName, ChannelHandlerContext ctx) throws FileNotFoundException {
        File f = new File(fileName);
        if (!f.exists()) {
            send("505 File doesn't exists " + fileName, ctx, fileName);
            throw new FileNotFoundException("the file doesn't exists");
        }
        FileInputStream finput = new FileInputStream(f);
        return finput;
    }

    private void sendData(FileInputStream fileInputStream, Socket socket) throws IOException {
        BufferedOutputStream dataOut = new BufferedOutputStream(socket.getOutputStream());
        int length;
        byte[] b = new byte[1024];
        while ((length = fileInputStream.read(b)) != -1) {
            dataOut.write(b, 0, length);
        }
        dataOut.close();
    }
}
