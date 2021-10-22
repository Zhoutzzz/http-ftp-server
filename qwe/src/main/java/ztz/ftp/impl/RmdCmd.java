/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class RmdCmd extends AbstractFTPCommand {

	public RmdCmd() {
		super("RMD");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("550 " + args + ": no such file or directory", ctx, args);
	}

}
