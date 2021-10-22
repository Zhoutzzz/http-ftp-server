/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class TypeCmd extends AbstractFTPCommand {

	public TypeCmd() {
		super("TYPE");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		if ("I".equals(args))
			send("200 Type set to IMAGE NONPRINT", ctx,args);
		else if ("A".equals(args))
			send("200 Type set to ASCII NONPRINT", ctx, args);
		else
			send("504 Command not implemented for that parameter", ctx, args);

	}

}
