package ztz.ftp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import ztz.ftp.cmd.DefaultCommandExecutionTemplate;

/**
 * @author zhoutzzz
 */
public class FtpChannelInit extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CrlfStringDecoder());
        pipeline.addLast(new FtpServerHandler(new DefaultCommandExecutionTemplate(new DefaultReceiver())));
    }
}
