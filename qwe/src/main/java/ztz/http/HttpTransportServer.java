package ztz.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpTransportServer extends ChannelInboundHandlerAdapter {

    private HttpRequest request;
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-\\._]?[^<>&\\\"]*");
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private static final String uploadUrl = "/up";

    private static final String fromFileUrl = "/post_multipart";

    private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

    private HttpPostRequestDecoder decoder;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true; // should delete file
        // on exit (in normal
        // exit)
        DiskFileUpload.baseDirectory = null; // system temp directory
        DiskAttribute.deleteOnExitTemporaryFile = true; // should delete file on
        // exit (in normal exit)
        DiskAttribute.baseDirectory = null; // system temp directory
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.request = (HttpRequest) msg;

        URI uri = new URI(request.uri());

        System.out.println(uri);

        urlRoute(ctx, uri.getPath());

        if (decoder != null) {
            decoder.setDiscardThreshold(0);

//            decoder.offer((HttpContent) msg);
            readHttpDataChunkByChunk();
            System.out.println("LastHttpContent");
            reset();
            String res = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h1>上传成功</h1>" +
                    "<a href='/'>返回首页</a>" +
                    "</body>\n" +
                    "</html>";
            writeResponse(ctx, res);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    // url路由
    private void urlRoute(ChannelHandlerContext ctx, String uri) {

        StringBuilder urlResponse = new StringBuilder();

        // 访问文件上传页面
        if (uri.startsWith(uploadUrl)) {

            urlResponse.append(getUploadResponse());

        } else if (uri.startsWith(fromFileUrl)) {

            decoder = new HttpPostRequestDecoder(factory, request);

//            return;

        } else {
//            urlResponse.append(getHomeResponse());
            try {
                fileListenAndDownload(ctx, request);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            return;
        }

//        writeResponse(ctx, urlResponse.toString());

    }

    private void writeResponse(ChannelHandlerContext ctx, String context) {

        ByteBuf buf = Unpooled.copiedBuffer(context, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        response.headers().set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);


        //设置短连接 addListener 写完马上关闭连接
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    private String getUploadResponse() {

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<form action=\"http://127.0.0.1:10001/post_multipart\" enctype=\"multipart/form-data\" method=\"POST\">\n" +
                "\n" +
                "\n" +
                "    <input type=\"file\" name=" +
                " " +
                "" +
                "\"YOU_KEY\">\n" +
                "\n" +
                "    <input type=\"submit\" name=\"send\">\n" +
                "\n" +
                "</form>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

    }


    private void readHttpDataChunkByChunk() throws IOException {
        InterfaceHttpData data = decoder.next();

        if (data != null) {

            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {

                FileUpload fileUpload = (FileUpload) data;

                if (fileUpload.isCompleted()) {

                    fileUpload.isInMemory();// tells if the file is in Memory
                    // or on File
                    fileUpload.renameTo(new File(PathUtil.getFileBasePath() + "/" + fileUpload.getFilename())); // enable to move into another
                    // File dest
                    decoder.removeHttpDataFromClean(fileUpload); //remove

                }


            }

        }

//        while (decoder.hasNext()) {
//
//
//        }

    }

    private void reset() {

        request = null;

        // destroy the decoder to release all resources
        decoder.destroy();

        decoder = null;

    }


    static final class PathUtil {
        private static final ClassLoader classLoader = PathUtil.class.getClassLoader();

        public static String getFileBasePath() {
            String os = System.getProperty("os.name");
            String basePath;
            basePath = "/Users/zhoutzzz/Desktop";
//            if (os.toLowerCase().startsWith("win")) {
//                basePath = "D:/warehouse/";
//            } else {
//                basePath = "/root/upload_source";
//            }
            basePath = basePath.replace("/", File.separator);
            return basePath;
        }
    }

    private void fileListenAndDownload(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        String uri = request.uri();
        String path = sanitizeUri(uri);
        File f = new File(path);
        if (f.isDirectory()) {
            if (uri.endsWith("/")) {
                sendListing(ctx, f, f.getPath());
            } else {
                DefaultFullHttpResponse red = new DefaultFullHttpResponse(HTTP_1_1, FOUND, Unpooled.EMPTY_BUFFER);
                red.headers().set(HttpHeaderNames.LOCATION, uri + "/");
                this.sendAndCleanupConnection(ctx, red);
            }
            return;
        }
        if (!f.isFile()) {
            sendAndCleanupConnection(ctx, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST, Unpooled.EMPTY_BUFFER));

            return;
        }
        if (request.method().equals(HttpMethod.HEAD)) {
            DefaultHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, f.length());
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, new MimetypesFileTypeMap().getContentType(f.getPath()));
            response.headers().set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
            ctx.write(response);
            return;
        }

        RandomAccessFile file = new RandomAccessFile(f, "r");
        long contentLength = file.length();
        long start = 0, end = 0;
        HttpResponseStatus status = PARTIAL_CONTENT;
        String rangeHead = request.headers().get(HttpHeaderNames.RANGE);
        if (rangeHead != null) {
            String[] ranges = parseHead(request);
            if (ranges.length > 1) {
                sendAndCleanupConnection(ctx, new DefaultFullHttpResponse(HTTP_1_1, REQUESTED_RANGE_NOT_SATISFIABLE, Unpooled.EMPTY_BUFFER));
                return;
            }
            for (String string : ranges) {
                String[] r = string.split("-");
                start = Integer.parseInt(r[0]);
                if (r.length > 1) {
                    end = Integer.parseInt(r[1]);
                    if (Integer.parseInt(r[1]) == start) {
                        end = contentLength;
                    }
                } else {
                    end = contentLength;
                }
            }
            if (end < 0 || end > contentLength) {
                status = REQUESTED_RANGE_NOT_SATISFIABLE;
            }
//            file.seek(start);
        } else {
            status = OK;
            end = contentLength;
        }

//        File tmp = new File(this.getClass().getClassLoader().getResource("").getPath() + "tmp");
//        tmp.deleteOnExit();
//        tmp.createNewFile();
//
//        FileOutputStream fileStream = new FileOutputStream(tmp);
//        int i;
//        byte[] b = new byte[1024];
//        while ((i = file.read(b)) != -1) {
//            fileStream.write(b, 0, i);
//        }
//        file.close();
//        fileStream.close();
//
//        RandomAccessFile curFile = new RandomAccessFile(tmp, "r");
//        long length = curFile.length();
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);

        HttpUtil.setContentLength(response, end - start);
        String contentType = fileTypeMap.getContentType(f.getPath());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
        if (rangeHead != null) {
            response.headers().set(HttpHeaderNames.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + contentLength);
        }
        ctx.write(response);

        //写出ChunkedFile
        ChunkedFile chunkedFile = new ChunkedFile(file, start, end - start + 1, 8192);
        ChannelFuture sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(chunkedFile), ctx.newProgressivePromise());
        //添加传输监听
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                if (total < 0) { // total unknown
                    System.err.println("Transfer progress: " + progress);
                } else {
                    System.err.println("Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.out.println("Transfer complete.");
//                curFile.close();
//                tmp.delete();
            }
        });

//        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//
//// Decide whether to close the connection or not.
//        if (!HttpHeaders.isKeepAlive(request)) {
//// Close the connection when the whole content is written out.
//            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
//        }

    }

    private static String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        // Convert to absolute path.
        return SystemPropertyUtil.get("user.dir") + File.separator + uri;
    }

    private void sendListing(ChannelHandlerContext ctx, File dir, String dirPath) {
        StringBuilder buf = new StringBuilder()
                .append("<!DOCTYPE html>\r\n")
                .append("<html><head><meta charset='utf-8' /><title>")
                .append("Listing of: ")
                .append(dirPath)
                .append("</title></head><body>\r\n")

                .append("<h3>Listing of: ")
                .append(dirPath)
                .append("</h3>\r\n")

                .append("<ul>")
                .append("<li><a href=\"../\">..</a></li>\r\n");

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isHidden() || !f.canRead()) {
                    continue;
                }

                String name = f.getName();
                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                    continue;
                }

                buf.append("<li><a href=\"")
                        .append(name)
                        .append("\">")
                        .append(name)
                        .append("</a></li>\r\n");
            }
        }

        buf.append("</ul><a href='/up' >上传</a> </body></html>\r\n");

        ByteBuf buffer = ctx.alloc().buffer(buf.length());
        buffer.writeCharSequence(buf.toString(), CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        this.sendAndCleanupConnection(ctx, response);
    }

    private void sendAndCleanupConnection(ChannelHandlerContext ctx, FullHttpResponse response) {
        final HttpRequest request = this.request;
        final boolean keepAlive = HttpUtil.isKeepAlive(request);
        HttpUtil.setContentLength(response, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        if (!keepAlive) {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
        } else if (request.protocolVersion().equals(HTTP_1_0)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive) {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private String[] parseHead(HttpRequest request) {
        String rangeParam = request.headers().get(HttpHeaderNames.RANGE);
        if (rangeParam == null) {
            return new String[]{};
        }
        return rangeParam.split("=")[1].split(",");
    }
}
