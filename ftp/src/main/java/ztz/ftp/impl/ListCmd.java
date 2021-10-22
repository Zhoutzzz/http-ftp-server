/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.ActivePassiveSocketManager;
import ztz.ftp.cmd.FTPAttrKeys;
import ztz.ftp.cmd.FTPCommand;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ListCmd extends AbstractFTPCommand {

private final ActivePassiveSocketManager activePassiveSocketManager;
	
	public ListCmd(ActivePassiveSocketManager activePassiveSocketManager) {
		super("LIST");
		this.activePassiveSocketManager = activePassiveSocketManager;
	}
	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		Socket activeSocket = activePassiveSocketManager.getActiveSocket(ctx);
		ServerSocket passiveSocket = activePassiveSocketManager.getPassiveSocket(ctx);
		FTPCommand lastFTPCommand = ctx.channel().attr(FTPAttrKeys.LAST_COMMAND).get();
		String lastCommand = lastFTPCommand != null ? lastFTPCommand.getCmd() : null;
		
		if ("PORT".equals(lastCommand)) {
			if (null == activeSocket) {
				send("503 Bad sequence of commands", ctx, args);
				return;
			}
			send("150 Opening binary mode data connection for LIST " + args,
					ctx, args);
			try {
				writeListToClient(activeSocket.getOutputStream());
				send("226 Transfer complete for LIST", ctx, args);
			} catch (IOException e1) {
				logger.warn(
						"Exception thrown on writing through active socket: ["
								+ activeSocket + "]", e1);
				send("552 Requested file action aborted", ctx, args);
			} finally {
				activePassiveSocketManager.closeActiveSocket(ctx);
			}
		} else if ("PASV".equals(lastCommand)) {
			if (null == passiveSocket) {
				send("503 Bad sequence of commands", ctx,  args);
				return;
			}
			
			send("150 Opening binary mode data connection for LIST on port: "
					+ passiveSocket.getLocalPort(), ctx,  args);
			Socket clientSocket = null;
			try {
				clientSocket = passiveSocket.accept();
				writeListToClient(clientSocket.getOutputStream());
				clientSocket.getOutputStream().close();
				send("226 Transfer complete for LIST", ctx, args);
			} catch (IOException e1) {
				logger.warn(
						"Exception thrown on writing through passive socket: ["
								+ passiveSocket + "],"
								+ "accepted client socket: [" + clientSocket
								+ "]", e1);
				send("552 Requested file action aborted", ctx, args);
			} finally {
				activePassiveSocketManager.closePassiveSocket(ctx);
			}
		} else
			send("503 Bad sequence of commands", ctx,  args);
	}
	
	private void writeListToClient(OutputStream outputStream)
			throws IOException {
//		AbstractVirtualFile f = new AbstractVirtualFile("test",
//				Permission.READWRITE, Permission.WRITE, Permission.EXEC);
//		outputStream.write(f.getListString().getBytes(CharsetUtil.UTF_8));
//		outputStream.write("drwxrwxr-x   2 0     5     512 Nov 27  2012 .snap"
//				.getBytes());
		outputStream.write(CRLF);

	}

}
