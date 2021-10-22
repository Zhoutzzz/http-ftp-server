/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.ActivePassiveSocketManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PortCmd extends AbstractFTPCommand {

	
	private final ActivePassiveSocketManager activePassiveSocketManager;
	
	public PortCmd(ActivePassiveSocketManager activePassiveSocketManager) {
		super("PORT");
		this.activePassiveSocketManager = activePassiveSocketManager;
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		InetSocketAddress addr = parsePortArgs(args);
		Socket activeSocket = activePassiveSocketManager.getActiveSocket(ctx);
		if (logger.isTraceEnabled())
			logger.trace(String.valueOf(addr));
		if (null == addr)
			send("501 Syntax error in parameters or arguments", ctx, args);
		
		else if (null != activeSocket && !activeSocket.isClosed())
			send("503 Bad sequence of commands", ctx, args);
		else {
			try {
				activePassiveSocketManager.openActiveSocket(ctx, addr);
				send("200 PORT command successful", ctx, args);
			} catch (IOException e1) {
				logger.warn(
						"Exception thrown on opening active socket to address: ["
								+ addr + "]", e1);
				activePassiveSocketManager.closeActiveSocket(ctx);
				send("552 Requested file action aborted", ctx, args);
			}
		}
	}




	

	
	private static InetSocketAddress parsePortArgs(String portArgs) {
		String[] strParts = portArgs.split(",");
		if (strParts.length != 6)
			return null;
		byte[] address = new byte[4];
		int[] parts = new int[6];
		for (int i = 0; i < 6; i++) {
			try {
				parts[i] = Integer.parseInt(strParts[i]);
			} catch (NumberFormatException e) {
				return null;
			}
			if (parts[i] < 0 || parts[i] > 255)
				return null;
		}
		for (int i = 0; i < 4; i++)
			address[i] = (byte) parts[i];
		int port = parts[4] << 8 | parts[5];
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByAddress(address);
		} catch (UnknownHostException e) {
			return null;
		}
		return new InetSocketAddress(inetAddress, port);
	}

}
