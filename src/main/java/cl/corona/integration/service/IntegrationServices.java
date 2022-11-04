package cl.corona.integration.service;


import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IntegrationServices {

    @Value("${sftp.ip}")
    private String sftpip;

    @Value("${sftp.prt}")
    private int sftpprt;

    @Value("${sftp.usr}")
    private String sftpusr;

    @Value("${sftp.pss}")
    private String sftppss;

    @Value("${sftp.org}")
    private String sftporg;

    @Value("${sftp.dst}")
    private String sftpdtn;

    @Value("${sftp.file}")
    private String namefile;

    @Value("${separador.carpetas}")
    private String separador;

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationServices.class);
    String strDir = System.getProperty("user.dir");

    public void connect() throws IOException {

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

            //final String LocalFile = "mtcsdirplord_20221026021027.zip";

            final String path = strDir + separador + sftpdtn + separador;

            //sftp.cd(sftporg);
            //sftp.get(LocalFile, path);

            //sftp.exit();

            Vector<ChannelSftp.LsEntry> entries = sftp.ls(sftporg);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry en : entries) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(en.getFilename());
                String sSubCadena = filename.substring(0,9).toUpperCase();

                if (sSubCadena.equals("SDIRTVDTE")) {
                    LOG.info("Downloading " + (sftporg + en.getFilename()) + " ----> " + path + en.getFilename());
                    sftp.get(sftporg + en.getFilename(), path + File.separator + en.getFilename());
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
