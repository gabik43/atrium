package atrium.fx.ru.arhive;

import atrium.fx.ru.AppConfiguration;
import atrium.fx.ru.InternalInterface;
import atrium.fx.ru.LogDB;
import atrium.fx.ru.core.ClientApplication;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by localadmin on 08.09.2015.
 */
public class ZIPArchive extends Thread{

    public List<File> listFileForArhive;
    public File OutPath;

    public void run() {
        start(listFileForArhive, OutPath);
        InternalInterface.showDelayedWindow();
    }

    public static void start(List<File> listFileForArhive, File OutPath){
        try
        {
            int len;
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(OutPath));
            byte[] buf = new byte[1024];
            for (File fileCurrent : listFileForArhive){
                InputStream in = new FileInputStream(fileCurrent);

                // create zip file structure
                String filePath = fileCurrent.getPath();
                filePath = filePath.substring(filePath.indexOf(AppConfiguration.FOLDER_FILE) + AppConfiguration.FOLDER_FILE.length() + 1);

                out.putNextEntry(new ZipEntry(filePath));
                while ((len=in.read(buf))>0)
                {
                    out.write(buf,0,len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            //tempFile.delete();

        }catch (IOException e)
        {e.printStackTrace();}
    }
}
