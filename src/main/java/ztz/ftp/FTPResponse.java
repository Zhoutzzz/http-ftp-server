/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp;

public enum FTPResponse {
	RESTART_MARKER_REPLY(110,"Restart marker reply."),
	FILE_STATUS_OKAY(150,"File status okay; about to open data connection."),
	SYNTAX_ERROR(501,"Syntax error");
	
	final private int code;
	final private String text;
	
	FTPResponse(int code,String text) {
		this.code  = code;
		this.text = text;
	}
	
	protected int getCode() {
		return code;
	}

	protected String getText() {
		return text;
	}

}
