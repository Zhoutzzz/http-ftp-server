/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.ActivePassiveSocketManager;

public class QuitCmd extends AbstractFTPCommand {

	private final ActivePassiveSocketManager activePassiveSocketManager;
	
	public QuitCmd(ActivePassiveSocketManager activePassiveSocketManager) {
		super("QUIT");
		this.activePassiveSocketManager = activePassiveSocketManager;
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("221 Service closing control connection.", ctx, args);
		activePassiveSocketManager.closeActiveSocket(ctx);
		activePassiveSocketManager.closePassiveSocket(ctx);
		ctx.close();
	}

}
