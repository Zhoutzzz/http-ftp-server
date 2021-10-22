/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;
import ztz.ftp.cmd.FTPAttrKeys;
import ztz.ftp.cmd.LogonCommand;

public class UserCmd extends AbstractFTPCommand implements LogonCommand {

	public UserCmd() {
		super("USER");
	}
	
	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("230 USER LOGGED IN", ctx, args);
		ctx.channel().attr(FTPAttrKeys.LOGGED_IN).set(true);
	}
}
