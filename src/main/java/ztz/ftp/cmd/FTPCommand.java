/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;

import io.netty.channel.ChannelHandlerContext;

public interface FTPCommand {
	
	String getCmd();
	void execute(ChannelHandlerContext ctx, String args);
	
}
