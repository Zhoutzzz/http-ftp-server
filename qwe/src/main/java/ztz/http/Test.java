package ztz.http;

import sun.net.www.protocol.http.HttpURLConnection;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * @author zhoutzzz
 */
public class Test {

    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:10001/gradle/wrapper/obs-mac-26.1.2.dmg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            connection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            connection.setReadTimeout(2000);
            connection.connect();
            long contentLength = Long.parseLong(connection.getHeaderField("content-Length"));

            File file = new File("/Users/Zhoutzzz/Desktop/newFile.dmg");
            long init = contentLength / 4;
            long start = 0, end = contentLength;
            Thread thread = new Thread(new DownloadTask(start, end, file));
            thread.start();
//            for (int i = 0; i < 4; i++) {
//                start = end;
//                end += init;
//                while (DownloadTask.ok) {
//                    Thread.yield();
//                }
//                System.out.println(thread.getName());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DownloadTask implements Runnable {
    public static boolean ok = true;
    private final long start;
    private final long end;
    private final File f;

    public DownloadTask(long start, long end, File f) {
        this.start = start;
        this.end = end;
        this.f = f;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("http://localhost:10001/gradle/wrapper/obs-mac-26.1.2.dmg");
            HttpURLConnection connection = new HttpURLConnection(url, null);
            connection.setRequestProperty("Range", "bytes=" + start + "-");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            connection.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            connection.setReadTimeout(2000);
            InputStream inputStream = connection.getInputStream();
            byte[] reads = new byte[1024];
            int len;
            RandomAccessFile file = new RandomAccessFile(f, "rwd");
            file.seek(start);
            ok = true;
            while (inputStream.read(reads) != -1) {
//                if (len < 1024) {
//                    byte[] copy = new byte[len];
//                    System.arraycopy(reads, 0, copy, 0, len);
//                    reads = copy;
//                }
//                file.write(reads, 0, len);
                file.write(reads);
            }
            ok = false;
            file.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
