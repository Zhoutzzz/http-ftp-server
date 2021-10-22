/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class NoopCmd extends AbstractFTPCommand {

	public NoopCmd() {
		super("NOOP");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("200 OK", ctx, args);
	}

}
