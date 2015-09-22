package ru.asteros.atrium.infoarhive;

import com.xhive.dom.interfaces.XhiveLibraryIf;

/**
 * Created by A.Gabdrakhmanov on 31.07.2015.
 */
public final class DataLoader {

    private DataLoader() {
        // Should not be instantiated
    }

    public static XhiveLibraryIf createLibrary(XhiveLibraryIf parent, String libraryName) {
        // create a library (unless it already exists)
        XhiveLibraryIf newLib = null;
        if (parent.nameExists(libraryName)) {
            newLib = (XhiveLibraryIf)parent.get(libraryName);
        } else {
            // create a library
            newLib = parent.createLibrary();

            // give the new library a name
            newLib.setName(libraryName);

            // append the new libary to its parent
            parent.appendChild(newLib);
        }

        return newLib;
    }

}
