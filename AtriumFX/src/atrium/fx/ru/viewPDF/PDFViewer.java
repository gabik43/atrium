package atrium.fx.ru.viewPDF;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by localadmin on 08.09.2015.
 */
public class PDFViewer {
    public static void View(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
