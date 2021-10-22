/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;

import io.netty.util.AttributeKey;

import java.net.ServerSocket;
import java.net.Socket;

public class FTPAttrKeys  {
	
	
	public static final AttributeKey<String> CWD = AttributeKey.newInstance("CWD");
	
	public static final AttributeKey<Boolean> LOGGED_IN = AttributeKey.newInstance("LOGGED_IN");
	public static final AttributeKey<Socket> ACTIVE_SOCKET = AttributeKey.newInstance("ACTIVE_SOCKET");
	public static final AttributeKey<ServerSocket> PASSIVE_SOCKET = AttributeKey.newInstance("PASSIVE_SOCKET");
	
	public static final AttributeKey<FTPCommand> LAST_COMMAND = AttributeKey.newInstance("LAST_FTP_COMMAND");
	
}
