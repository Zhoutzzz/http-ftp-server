/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ActivePassiveSocketManager {

	private static final Logger logger = LoggerFactory
			.getLogger(ActivePassiveSocketManager.class);
	
	private final byte[] passiveAddress;
	private final int lowestPassivePort;
	private final int highestPassivePort;
	private final int passiveOpenAttempts;

	public ActivePassiveSocketManager(byte[] passiveAddress,
			int lowestPassivePort, int highestPassivePort,
			int passiveOpenAttempts) {
		super();
		
		if (lowestPassivePort <= 0 || lowestPassivePort >= 1 << 16)
			throw new IllegalArgumentException("Provided lowestPassivePort: ["
					+ lowestPassivePort + "] ia out of valid range");
		if (highestPassivePort <= 0 || highestPassivePort >= 1 << 16)
			throw new IllegalArgumentException("Provided highestPassivePort: ["
					+ highestPassivePort + "] ia out of valid range");
		if (lowestPassivePort > highestPassivePort)
			throw new IllegalArgumentException("Provided lowestPassivePort: ["
					+ lowestPassivePort + "] must be not greater than "
					+ "highestPassivePort: [" + highestPassivePort + "]");

		if (passiveOpenAttempts <= 0)
			throw new IllegalArgumentException(
					"Provided passiveOpenAttempts: [" + passiveOpenAttempts
							+ "] must be positive");

		
		
		this.passiveAddress = passiveAddress;
		this.lowestPassivePort = lowestPassivePort;
		this.highestPassivePort = highestPassivePort;
		this.passiveOpenAttempts = passiveOpenAttempts;
	}

	
	
	public Socket getActiveSocket(ChannelHandlerContext ctx) {
		return ctx.channel().attr(FTPAttrKeys.ACTIVE_SOCKET).get();
	}
	
	
	
	public void closeActiveSocket(ChannelHandlerContext ctx) {
		Socket activeSocket = ctx.channel().attr(FTPAttrKeys.ACTIVE_SOCKET).get();
		if (null == activeSocket)
			return;
		try {
			activeSocket.close();
		} catch (Exception e) {
			logger.warn("Exception thrown on closing active socket", e);
		} finally {
			activeSocket = null;
		}
	}
	
	public void openActiveSocket(ChannelHandlerContext ctx,
			InetSocketAddress addr) throws IOException {
		Socket activeSocket;
		activeSocket = new Socket(addr.getAddress(),
				addr.getPort());
		ctx.channel().attr(FTPAttrKeys.ACTIVE_SOCKET).set(activeSocket);
	}
	
	public ServerSocket getPassiveSocket(ChannelHandlerContext ctx) {
		return  ctx.channel().attr(FTPAttrKeys.PASSIVE_SOCKET).get();
	}
	
	public ServerSocket openPassiveSocket(ChannelHandlerContext ctx,int port, InetAddress addr)
			throws IOException {
		ServerSocket passiveSocket;
		passiveSocket = new ServerSocket(port, 50, addr);
		ctx.channel().attr(FTPAttrKeys.PASSIVE_SOCKET).set(passiveSocket);
		return passiveSocket;
	}
	
	public void closePassiveSocket(ChannelHandlerContext ctx) {
		ServerSocket passiveSocket = ctx.channel().attr(FTPAttrKeys.PASSIVE_SOCKET).getAndSet(null);
		if (null == passiveSocket)
			return;
		try {
			passiveSocket.close();
		} catch (Exception e) {
			logger.warn("Exception thrown on closing passive socket", e);
		} 
	}

	public int getMaxPassiveOpenAttempts() {
		return passiveOpenAttempts;
	}

	public int getLowestPassivePort() {
		return lowestPassivePort;
	}

	public int getHighestPassivePort() {
		return highestPassivePort;
	}

	public byte[] getPassiveAddress() {
		return passiveAddress;
	}
	
}
