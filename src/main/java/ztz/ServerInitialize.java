package ztz;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import ztz.http.HttpTransportServer;

public class ServerInitialize extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
//        pipeline.addLast(new DownOrUp());

        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new HttpObjectAggregator(655300000));
        pipeline.addLast("chunk", new ChunkedWriteHandler());
        pipeline.addLast("down", new HttpTransportServer());
//        pipeline.addLast(new HttpUploadServer());
//        pipeline.addLast(new HttpStaticFileServerHandler());
    }
}
