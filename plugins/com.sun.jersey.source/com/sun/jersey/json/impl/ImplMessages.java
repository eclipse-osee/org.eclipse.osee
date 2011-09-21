
package com.sun.jersey.json.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessageFactory;
import com.sun.jersey.localization.Localizer;


/**
 * Defines string formatting method for each constant in the resource file
 * 
 */
public final class ImplMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.jersey.json.impl.impl");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableERROR_JAXB_RI_2_1_12_MISSING() {
        return messageFactory.getMessage("error.jaxb.ri.2.1.12.missing");
    }

    /**
     * NATURAL JSON notation configured, but at least JAXB RI 2.1.12 is needed when XML attributes are in use. Please add jaxb-impl-2.1.12 to your classpath!
     * 
     */
    public static String ERROR_JAXB_RI_2_1_12_MISSING() {
        return localizer.localize(localizableERROR_JAXB_RI_2_1_12_MISSING());
    }

    public static Localizable localizableERROR_WRITING_JSON_ARRAY() {
        return messageFactory.getMessage("error.writing.json.array");
    }

    /**
     * Error writing JSON array.
     * 
     */
    public static String ERROR_WRITING_JSON_ARRAY() {
        return localizer.localize(localizableERROR_WRITING_JSON_ARRAY());
    }

    public static Localizable localizableERROR_JAXB_RI_2_1_10_MISSING() {
        return messageFactory.getMessage("error.jaxb.ri.2.1.10.missing");
    }

    /**
     * NATURAL JSON notation configured, but JAXB RI 2.1.10 not found. For the recent builds to get this working correctly, you need even at least JAXB version 2.1.12. Please add it to your classpath!
     * 
     */
    public static String ERROR_JAXB_RI_2_1_10_MISSING() {
        return localizer.localize(localizableERROR_JAXB_RI_2_1_10_MISSING());
    }

    public static Localizable localizableERROR_WRITING_JSON_OBJECT() {
        return messageFactory.getMessage("error.writing.json.object");
    }

    /**
     * Error writing JSON object.
     * 
     */
    public static String ERROR_WRITING_JSON_OBJECT() {
        return localizer.localize(localizableERROR_WRITING_JSON_OBJECT());
    }

    public static Localizable localizableERROR_PARSING_JSON_ARRAY() {
        return messageFactory.getMessage("error.parsing.json.array");
    }

    /**
     * Error parsing JSON array.
     * 
     */
    public static String ERROR_PARSING_JSON_ARRAY() {
        return localizer.localize(localizableERROR_PARSING_JSON_ARRAY());
    }

    public static Localizable localizableERROR_PARSING_JSON_OBJECT() {
        return messageFactory.getMessage("error.parsing.json.object");
    }

    /**
     * Error parsing JSON object.
     * 
     */
    public static String ERROR_PARSING_JSON_OBJECT() {
        return localizer.localize(localizableERROR_PARSING_JSON_OBJECT());
    }

    public static Localizable localizableERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.jsonp.msg.body.writer.not.found", arg0, arg1);
    }

    /**
     * A message body writer for Java type, {0}, and MIME media type, {1}, was not found.
     * 
     */
    public static String ERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return localizer.localize(localizableERROR_JSONP_MSG_BODY_WRITER_NOT_FOUND(arg0, arg1));
    }

    public static Localizable localizableERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.nonge.jsonp.msg.body.writer.not.found", arg0, arg1);
    }

    /**
     * A message body writer for Java type, {0}, and MIME media type, {1}, was not found. If you want to serialize a parametrized type, you might want to use GenericEntity
     * 
     */
    public static String ERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(Object arg0, Object arg1) {
        return localizer.localize(localizableERROR_NONGE_JSONP_MSG_BODY_WRITER_NOT_FOUND(arg0, arg1));
    }

}
