/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.FTPAttrKeys;

public class PwdCmd extends AbstractFTPCommand {

	
	public PwdCmd() {
		super("PWD");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		String curDir = ctx.channel().attr(FTPAttrKeys.CWD).get();
		send(String.format("257 \"%s\" is current directory",curDir == null ? "/" : curDir),ctx,args);
	}

}
