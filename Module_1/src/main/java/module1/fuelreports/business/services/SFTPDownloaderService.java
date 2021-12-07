package module1.fuelreports.business.services;

import com.jcraft.jsch.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SFTPDownloaderService {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger( SFTPDownloaderService.class.getSimpleName() );

    private final String REMOTE_HOST = "fe.ddns.protal.biz";
    private final String USERNAME = "sftpuser";
    private final String PASSWORD = "hyperpass";
    private final String REMOTE_DIRECTORY = "/xml-data/";
    private final int CONNECT_TIMEOUT = 5000;
    private final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    private String DATA_FOLDER_PATH;

    public void downloadData() {
//        DATA_FOLDER_PATH = url;
        DATA_FOLDER_PATH = getPath();
        JSch jsch = new JSch();
        Session session = null;

        try {
            Properties config = new Properties();
            config.put(STRICT_HOST_KEY_CHECKING, "no");
            session = jsch.getSession(USERNAME, REMOTE_HOST);
            session.setPassword(PASSWORD);
            session.setConfig(config);
            session.connect(CONNECT_TIMEOUT);

            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            Vector fileList = channelSftp.ls(REMOTE_DIRECTORY);
            LOGGER.setLevel(Level.INFO);
            LOGGER.info("Number of files in the remote server: " + fileList.size());

            int countOfDownloadedXmlFiles = 0;

            //downloading the data into the resources/data directory where later they will be parsed
            for ( int i = 0; i < fileList.size()/150; i++ ) {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) fileList.get(i);
                if (lsEntry.getFilename().contains(".xml")) {
                    channelSftp.get(REMOTE_DIRECTORY + lsEntry.getFilename(), DATA_FOLDER_PATH);
                    countOfDownloadedXmlFiles++;
                }
            }

            LOGGER.info(String.format("We have successfully downloaded %d .xml files.%n", countOfDownloadedXmlFiles));

            channelSftp.disconnect();
            session.disconnect();
        } catch (JSchException | SftpException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private String getPath() {
        DatabaseConnectionService dbs = new DatabaseConnectionService();

        dbs.connectMainUrl();
        String query = "SELECT `info` FROM config";
        String path = "";
        try {
            ResultSet rs = dbs.getStmt().executeQuery(query);
            rs.next();
            path = rs.getString("info");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "/" + path;
    }
}
