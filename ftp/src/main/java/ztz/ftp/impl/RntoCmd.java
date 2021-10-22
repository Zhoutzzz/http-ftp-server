/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class RntoCmd extends AbstractFTPCommand {

	public RntoCmd() {
		super("RNTO");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("250 RNTO command successful", ctx, args);
	}

}
