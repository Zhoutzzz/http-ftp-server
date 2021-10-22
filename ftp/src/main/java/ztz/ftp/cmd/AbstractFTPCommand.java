/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFTPCommand implements FTPCommand {

	
	protected static final byte CR = 13;
	protected static final byte LF = 10;
	protected static final byte[] CRLF = new byte[] { CR, LF };
	
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final String cmd;
	
	public AbstractFTPCommand(String cmd) {
		// just make sure the commands are in upper case
		this.cmd = cmd.trim().toUpperCase();
	}
	
	public String getCmd() {
		return this.cmd;
	}
	
	protected void send(String response, ChannelHandlerContext ctx, String args) {
		if (logger.isDebugEnabled()) {
			logger.debug("-> {} {}",cmd,args);
			logger.debug("<- {}", response);
		}
		CommandUtil.send(response, ctx);
	}

	

	

}
