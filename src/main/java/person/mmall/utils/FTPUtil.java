package person.mmall.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    public FTPUtil(String ip, int port, String user, String pssword) {
        this.Ip = ip;
        this.port = port;
        this.user = user;
        this.pssword = pssword;
    }

    public static boolean uploadFile(List<File> fileList){

        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPassword);
        logger.info("开始连接到文件服务器");
        if(ftpUtil.uploadFile("img", fileList))
        {
            logger.info("上传文件成功");
            return true;
        }

        return true;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) {

        boolean isUpload = true;
        FileInputStream fileInputStream;

        if (connectToServer(this.Ip, this.port, this.user, this.pssword)) {
            logger.info("成功连接到服务器");
            try {
                    this.ftpClient.changeWorkingDirectory(remotePath);
                    this.ftpClient.setBufferSize(1024);
                    this.ftpClient.setControlEncoding("UTF-8");
                    this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    this.ftpClient.enterLocalPassiveMode();

                    logger.info("开始上传文件");
                    for (File file : fileList){
                        fileInputStream = new FileInputStream(file);
                         this.ftpClient.storeFile(file.getName(), fileInputStream);

                         fileInputStream.close();
                    }
            } catch (IOException e) {
                isUpload = false;
                logger.error("上传文件失败：", e);
            }
        }

        return isUpload;
    }

    private boolean connectToServer(String ip, int port, String user, String passwrod){

        boolean isSuccess = false;

        this.ftpClient = new FTPClient();

        try {
            this.ftpClient.connect(ip);
            isSuccess = this.ftpClient.login(user, passwrod);
        } catch (IOException e) {

            logger.error("连接到文件服务器异常", e);
        }

        return isSuccess;
    }

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static final String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static final String ftpPassword = PropertiesUtil.getProperty("ftp.pass");

    private String Ip;
    private int port;
    private String user;
    private String pssword;
    private FTPClient ftpClient;
}
