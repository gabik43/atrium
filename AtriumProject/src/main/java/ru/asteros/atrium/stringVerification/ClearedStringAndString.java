package ru.asteros.atrium.stringVerification;

/**
 * Created by A.Gabdrakhmanov on 27.09.2015.
 * Структура хранящая очищенную от недопустимых символов строку, строку с запрещенными символывами и факт нахождения
 * запрещенных символов в строке.
 */
public class ClearedStringAndString {
    public final String clearedString;
    public final String stringWithPermissionSymbols;
    public final boolean isFindPermSymbols;
    public ClearedStringAndString (String clearedString, String stringWithPermissionSymbols, boolean isFindPermSymbols){
        this.clearedString = clearedString;
        this.stringWithPermissionSymbols = stringWithPermissionSymbols;
        this.isFindPermSymbols = isFindPermSymbols;
    }
}
