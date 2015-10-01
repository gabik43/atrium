import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by localadmin on 09.09.2015.
 */
public class test {

    public static int i = getInt();

    public static void main(String[] args) throws PrintException, IOException {

        System.out.println(test.i);

    }

    public static int getInt() {
        return i;
    }

}