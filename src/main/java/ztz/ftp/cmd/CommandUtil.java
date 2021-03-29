/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

class CommandUtil {
	
	public static void send(String response, ChannelHandlerContext ctx) {
		String line = response + "\r\n";
		byte[] data = line.getBytes(CharsetUtil.US_ASCII);
		ctx.channel().writeAndFlush(Unpooled.wrappedBuffer(data));
	}

	
}
