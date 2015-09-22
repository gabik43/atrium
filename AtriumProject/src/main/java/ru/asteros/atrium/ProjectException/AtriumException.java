package ru.asteros.atrium.ProjectException;

/**
 * Created by A.Gabdrakhmanov on 19.08.2015.
 */
public class AtriumException extends Exception {
    /*Тип ошибки. Набор ошибок находится в классе AtriumError*/
    public String typeError;


    public AtriumException(String typeError) {
        super();
        this.typeError = typeError;
    }

    @Override
    public String getMessage() {
        return typeError;
    }
}


