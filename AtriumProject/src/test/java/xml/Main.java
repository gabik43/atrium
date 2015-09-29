package xml;

import ru.asteros.atrium.stringVerification.ClearedStringAndString;
import ru.asteros.atrium.stringVerification.StringVerification;

/**
 * Created by A.Gabdrakhmanov on 10.04.2015.
 */
public class Main {

    public static void main(String args[]) {
        String name = " ........42831312_7714072839_ВГТРК \\ГТРКСлавия_2015-08-31*.pdf. ";
        String error = "";
        ClearedStringAndString clearedStringAndString = StringVerification.getClearedStringAndStringWitchErrorSymbols(name);
        int i = 0;
    }
}
