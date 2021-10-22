/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.ActivePassiveSocketManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import static java.lang.System.currentTimeMillis;

public class PasvCmd extends AbstractFTPCommand {

	private final ActivePassiveSocketManager activePassiveSocketManager;


	public PasvCmd(ActivePassiveSocketManager activePassiveSocketManager) {
		super("PASV");
		this.activePassiveSocketManager = activePassiveSocketManager;
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		ServerSocket passiveSocket = null;
		int passiveOpenAttempts = activePassiveSocketManager.getMaxPassiveOpenAttempts();
		int lowestPassivePort = activePassiveSocketManager.getLowestPassivePort();
		int highestPassivePort = activePassiveSocketManager.getHighestPassivePort();
		byte[] passiveAddress = activePassiveSocketManager.getPassiveAddress();
		for (int i = 0; i < passiveOpenAttempts; i++) {
			int port = choosePassivePort(lowestPassivePort, highestPassivePort);
			int part1 = (byte) (port >> 8) & 0xff;
			int part2 = (byte) (port >> 0) & 0xff;
			InetAddress addr = null;
			try {
				addr = InetAddress.getByAddress(passiveAddress);
				passiveSocket = activePassiveSocketManager.openPassiveSocket(ctx,port, addr);
				send(String.format(
						"227 Entering Passive Mode (%d,%d,%d,%d,%d,%d)",
						passiveAddress[0], passiveAddress[1],
						passiveAddress[2], passiveAddress[3], part1, part2),
						ctx, args);
				break;
			} catch (IOException e1) {
				logger.warn(
						"Exception thrown on binding passive socket to address: ["
								+ addr + "], port: [" + port + "], "
								+ "attempt: [" + i + 1 + "] of: ["
								+ passiveOpenAttempts + "]", e1);
				activePassiveSocketManager.closePassiveSocket(ctx);
				// ensure port change
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					logger.warn("Exception while Thread.sleep()",e);
				}
			}
		}
		if (null == passiveSocket)
			send("551 Requested action aborted", ctx, args);

	}


	
	private static int choosePassivePort(int low, int high) {
		int length = high - low;
		int offset = (int) (currentTimeMillis() % length);
		return low + offset;
	}
	
	
	

}
