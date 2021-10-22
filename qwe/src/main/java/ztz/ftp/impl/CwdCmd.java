/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.FTPAttrKeys;

public class CwdCmd extends AbstractFTPCommand {

	public CwdCmd() {
		super("CWD");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		ctx.attr(FTPAttrKeys.CWD).set(args);
		send("250 CWD command successful", ctx, args);
	}

}
