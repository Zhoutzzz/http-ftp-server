/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommandExecutionTemplate {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	public abstract  FTPCommand  getFTPCommand(String cmd);
	
	
	public final void executeCommand(ChannelHandlerContext ctx, String cmd,String args) {
		//TODO handle properly FTP States see p.53 RFC 959
		FTPCommand command = getFTPCommand(cmd);
		if (command != null) {
			Boolean loggedIn = ctx.channel().attr(FTPAttrKeys.LOGGED_IN).get();
			boolean isLogonCommand = command instanceof LogonCommand;
			if ((!loggedIn && isLogonCommand) || (loggedIn && !isLogonCommand)) {
				command.execute(ctx, args);
				ctx.channel().attr(FTPAttrKeys.LAST_COMMAND).set(command);
				return;
			}
		}
		
		logger.debug("Command not supported {} {}",cmd,args);
		CommandUtil.send("500 Command not supported", ctx);
		
	}

}
