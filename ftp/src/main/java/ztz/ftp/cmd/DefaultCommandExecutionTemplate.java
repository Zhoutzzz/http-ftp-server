/**
 * source : https://github.com/codingtony/netty-ftp-receiver
 */
package ztz.ftp.cmd;


import ztz.ftp.DataReceiver;
import ztz.ftp.impl.*;

import java.util.*;

public class DefaultCommandExecutionTemplate extends CommandExecutionTemplate {

    private final Map<String, FTPCommand> SUPPORTED_COMMAND_SET;
    final ActivePassiveSocketManager activePassiveSocketManager;
    final DataReceiver dataReceiver;

    public static void main(String[] args) {
        int n = 2;
        for (;;) {
            n <<= 2;
        }
    }


    public DefaultCommandExecutionTemplate(DataReceiver dataReceiver) {
        this(new ActivePassiveSocketManager(new byte[]{127, 0, 0, 1}, 2121, 4242, 10), dataReceiver);
    }

	/*
	 *  
	 *  5.3.1.  FTP COMMANDS
	 *  The following are the FTP commands:

            USER <SP> <username> <CRLF>
            PASS <SP> <password> <CRLF>
            ACCT <SP> <account-information> <CRLF>
            CWD  <SP> <pathname> <CRLF>
            CDUP <CRLF>
            SMNT <SP> <pathname> <CRLF>
            QUIT <CRLF>
            REIN <CRLF>
            PORT <SP> <host-port> <CRLF>
            PASV <CRLF>
            TYPE <SP> <type-code> <CRLF>
            STRU <SP> <structure-code> <CRLF>
            MODE <SP> <mode-code> <CRLF>
            RETR <SP> <pathname> <CRLF>
            STOR <SP> <pathname> <CRLF>
            STOU <CRLF>
            APPE <SP> <pathname> <CRLF>
            ALLO <SP> <decimal-integer>
                [<SP> R <SP> <decimal-integer>] <CRLF>
            REST <SP> <marker> <CRLF>
            RNFR <SP> <pathname> <CRLF>
            RNTO <SP> <pathname> <CRLF>
            ABOR <CRLF>
            DELE <SP> <pathname> <CRLF>
            RMD  <SP> <pathname> <CRLF>
            MKD  <SP> <pathname> <CRLF>
            PWD  <CRLF>
            LIST [<SP> <pathname>] <CRLF>
            NLST [<SP> <pathname>] <CRLF>
            SITE <SP> <string> <CRLF>
            SYST <CRLF>
            STAT [<SP> <pathname>] <CRLF>
            HELP [<SP> <string>] <CRLF>
            NOOP <CRLF>
	 */

    @Override
    public FTPCommand getFTPCommand(String cmd) {
        return SUPPORTED_COMMAND_SET.get(cmd);
    }

    public DefaultCommandExecutionTemplate(
            final ActivePassiveSocketManager activePassiveSocketManager,
            final DataReceiver dataReceiver) {
        super();
        this.activePassiveSocketManager = activePassiveSocketManager;
        this.dataReceiver = dataReceiver;
        SUPPORTED_COMMAND_SET = Collections
                .unmodifiableMap(new HashMap<String, FTPCommand>() {
                    Set<String> usedCmd = new HashSet<String>();
                    private static final long serialVersionUID = 1L;

                    {
                        register(new CwdCmd());
                        register(new DeleCmd());

                        register(new ListCmd(activePassiveSocketManager));
                        register(new MkdCmd());
                        register(new NoopCmd());
                        register(new PasvCmd(activePassiveSocketManager));
                        register(new PortCmd(activePassiveSocketManager));
                        register(new PwdCmd());
                        register(new QuitCmd(activePassiveSocketManager));
                        register(new RmdCmd());
                        register(new RnfrCmd());
                        register(new RntoCmd());

                        register(new StorCmd(activePassiveSocketManager, dataReceiver));
                        register(new SystCmd());
                        register(new TypeCmd());
                        register(new UserCmd());
                        register(new RetrCmd(activePassiveSocketManager));
                        register(new RestCmd());
                    }

                    void register(FTPCommand cmd) {
                        String cmdStr = cmd.getCmd();
                        if (usedCmd.contains(cmdStr)) {
                            throw new IllegalArgumentException(String.format("Command already defined [%s] : %s", cmdStr, cmd.getClass().getName()));
                        }
                        put(cmdStr, cmd);

                    }
                });
    }

}
