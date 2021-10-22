package ztz.ftp.impl;

import io.netty.channel.ChannelHandlerContext;
import ztz.ftp.cmd.AbstractFTPCommand;

/**
 * @author zhoutzzz
 */
public class RestCmd extends AbstractFTPCommand {
    public RestCmd() {
        super("REST");
    }

    @Override
    public void execute(ChannelHandlerContext ctx, String args) {

    }
}
