
package com.sun.jersey.atom.rome.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessageFactory;
import com.sun.jersey.localization.Localizer;


/**
 * Defines string formatting method for each constant in the resource file
 * 
 */
public final class ImplMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.jersey.atom.rome.impl.impl");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableERROR_MARSHALLING_ATOM(Object arg0) {
        return messageFactory.getMessage("error.marshalling.atom", arg0);
    }

    /**
     * Error marshalling out Atom object of type "{0}".
     * 
     */
    public static String ERROR_MARSHALLING_ATOM(Object arg0) {
        return localizer.localize(localizableERROR_MARSHALLING_ATOM(arg0));
    }

    public static Localizable localizableERROR_NOT_ATOM_FEED(Object arg0) {
        return messageFactory.getMessage("error.not.atom.feed", arg0);
    }

    /**
     * Feed is Not of type Atom Feed : "{0}".
     * 
     */
    public static String ERROR_NOT_ATOM_FEED(Object arg0) {
        return localizer.localize(localizableERROR_NOT_ATOM_FEED(arg0));
    }

    public static Localizable localizableERROR_CREATING_ATOM(Object arg0) {
        return messageFactory.getMessage("error.creating.atom", arg0);
    }

    /**
     * Error creating  Atom object of type "{0}".
     * 
     */
    public static String ERROR_CREATING_ATOM(Object arg0) {
        return localizer.localize(localizableERROR_CREATING_ATOM(arg0));
    }

}
