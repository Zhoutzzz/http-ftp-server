/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class RnfrCmd extends AbstractFTPCommand {

	public RnfrCmd() {
		super("RNFR");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("350 File exists, ready for destination name", ctx, args);
	}

}
