/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

public class SystCmd extends AbstractFTPCommand {

	public SystCmd() {
		super("SYST");
	}

	@Override
	public void execute(ChannelHandlerContext ctx, String args) {
		send("215 UNIX Type: Java custom implementation", ctx,  args);
	}

}
