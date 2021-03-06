/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation should read all required data from provided FTP file-upload stream,
 * stream will be closed immediately after {@link #receive(String, InputStream)} call
 *
 * @author alexkasko
 * Date: 12/28/12
 */
public interface DataReceiver {
    /**
     * Implementation should read provided FTP file-upload data
     *
     * @param name name of uploaded file
     * @param data uploaded file stream
     * @throws IOException on IO error
     */
    default void receive(String name, InputStream data) throws IOException{};
}
