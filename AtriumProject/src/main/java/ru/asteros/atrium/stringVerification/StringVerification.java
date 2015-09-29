package ru.asteros.atrium.stringVerification;

/**
 * Created by localadmin on 21.08.2015.
 */
public class StringVerification {
    
    /*Функция возвращает строку, в которой удалены управляющие символы
    * @param inputString - входная строка, с возможными ошибочными полями
    * @return - пара: строка без запрещенных символов и строка с выделенными запрещенными символыми. Символы выделяются квадратными скобками*/
    public static ClearedStringAndString getClearedStringAndStringWitchErrorSymbols(String inputString){
        String stringWithoutPermissionInNamFileAndFolder = deletePermissionSymbolsInNameFileAndFolder(inputString);
        Boolean isFindPermissionSymbols = false;
        StringBuilder textDigits = new StringBuilder();
        StringBuilder textPermissionDigit = new StringBuilder();
        for (char ch : stringWithoutPermissionInNamFileAndFolder.toCharArray()) {
            if ( ch >= ' ') {
                textDigits.append(ch);
                textPermissionDigit.append(ch);
            } else {
                isFindPermissionSymbols = true;
                textPermissionDigit.append("[").append(ch).append("]");
            }
        }
        ClearedStringAndString clearedStringAndString = new ClearedStringAndString(textDigits.toString(), textPermissionDigit.toString(), isFindPermissionSymbols);
        return clearedStringAndString;
    }

    /*Удаление запрещенных в имени файла и папки символов, удаление точки и пробела в начале и конце строки*/
    private static String deletePermissionSymbolsInNameFileAndFolder(String inputString){
        inputString = deleteStartSymbols(inputString);
        inputString = deleteFinishSymbols(inputString);
        if (inputString.endsWith(".")){
            inputString = inputString.substring(0, inputString.length()-1);
        }
        if (inputString.startsWith(".")){
            inputString = inputString.substring(1, inputString.length());
        }
        return inputString.replaceAll("[\\\\/:*?\\\"<>|]", "");
    }

    private static String deleteStartSymbols(String inputString){
        if (inputString.startsWith(".") || inputString.startsWith(" ")){
            inputString = inputString.substring(1, inputString.length());
            return deleteStartSymbols(inputString);
        } else return inputString;
    }

    private static String deleteFinishSymbols(String inputString){
        if (inputString.endsWith(".") || inputString.endsWith(" ")) {
            inputString = inputString.substring(0, inputString.length()-1);
            return deleteFinishSymbols(inputString);
        } else return inputString;
    }
    
}
