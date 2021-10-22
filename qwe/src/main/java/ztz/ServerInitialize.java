package ztz;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import ztz.http.HttpTransportServer;

public class ServerInitialize extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
//        pipeline.addLast(new HttpRequestDecoder());
//        pipeline.addLast(new HttpResponseEncoder());

        pipeline.addLast(new HttpObjectAggregator(655300000));
//        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpTransportServer());
//        pipeline.addLast(new HttpUploadServer());
//        pipeline.addLast(new HttpStaticFileServerHandler());
    }
}
