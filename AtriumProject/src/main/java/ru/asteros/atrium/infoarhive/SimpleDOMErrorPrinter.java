package ru.asteros.atrium.infoarhive;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;

/**
 * Created by A.Gabdrakhmanov on 31.07.2015.
 */
public class SimpleDOMErrorPrinter implements DOMErrorHandler {

    @Override
    public boolean handleError(DOMError error) {
        String message = error.getMessage();
        short errorType = error.getSeverity();

        // Print out error information
        switch (errorType) {
            case DOMError.SEVERITY_WARNING:
                System.out.print("-->Warning: ");
                break;
            case DOMError.SEVERITY_ERROR:
                System.out.print("-->Error: ");
                break;
            case DOMError.SEVERITY_FATAL_ERROR:
                System.out.print("-->Fatal Error: ");
                break;
        }
        System.out.println(message);

        return true;
    }

}
