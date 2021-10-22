/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class MkdCmd extends AbstractFTPCommand {

	public MkdCmd() {
		super("MKD");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("521 \"" + args + "\" directory exists", ctx, args);
	}

}
