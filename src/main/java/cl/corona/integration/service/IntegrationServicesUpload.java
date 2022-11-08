package cl.corona.integration.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class IntegrationServicesUpload {
    @Value("${sftp2.ip}")
    private String sftpip;

    @Value("${sftp2.prt}")
    private int sftpprt;

    @Value("${sftp2.usr}")
    private String sftpusr;

    @Value("${sftp2.pss}")
    private String sftppss;

    @Value("${sftp2.org}")
    private String sftporg;

    @Value("${sftp2.dst}")
    private String sftpdtn;

    @Value("${sftp2.file}")
    private String namefile;

    @Value("${separador.carpetas}")
    private String separador;

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationServices.class);
    String strDir = System.getProperty("user.dir2");

    public void UploadFile() throws IOException {

        JSch jsch = new JSch();
        try {

            Session session = jsch.getSession(sftpusr, sftpip, sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.connect();

            final String pathOrigen = strDir + separador + sftpdtn + separador;

            Vector<ChannelSftp.LsEntry> outputs = sftp.ls(pathOrigen);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry sal : outputs) {
                if (sal.getFilename().equals(".") || sal.getFilename().equals("..") || sal.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(sal.getFilename());
                String sSubCadena = filename.substring(0,9).toUpperCase();

                if (sSubCadena.equals("SDIRTVDTE")) {
                    LOG.info("Uploading " + (sftporg + sal.getFilename()) + " ----> " + sftpdtn + sal.getFilename());
                    sftp.put(pathOrigen + sal.getFilename(), sftpdtn + File.separator + sal.getFilename());
                }
            }

            sftp.exit();

        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}",  e);
        } catch (SftpException e) {
            e.printStackTrace();
        }


    }
}
