
package com.sun.jersey.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessageFactory;
import com.sun.jersey.localization.Localizer;


/**
 * Defines string formatting method for each constant in the resource file
 * 
 */
public final class SpiMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.jersey.impl.spi");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableILLEGAL_CONFIG_SYNTAX() {
        return messageFactory.getMessage("illegal.config.syntax");
    }

    /**
     * Illegal configuration-file syntax.
     * 
     */
    public static String ILLEGAL_CONFIG_SYNTAX() {
        return localizer.localize(localizableILLEGAL_CONFIG_SYNTAX());
    }

    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("provider.could.not.be.created", arg0, arg1, arg2);
    }

    /**
     * The class {0} implementing provider {1} could not be instantiated: {2}
     * 
     */
    public static String PROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1, arg2));
    }

    public static Localizable localizableTEMPLATE_NAME_TO_VALUE_NOT_NULL() {
        return messageFactory.getMessage("template.name.to.value.not.null");
    }

    /**
     * Template name to value map cannot be null.
     * 
     */
    public static String TEMPLATE_NAME_TO_VALUE_NOT_NULL() {
        return localizer.localize(localizableTEMPLATE_NAME_TO_VALUE_NOT_NULL());
    }

    public static Localizable localizableILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
        return messageFactory.getMessage("illegal.provider.class.name", arg0);
    }

    /**
     * Illegal provider-class name: {0}.
     * 
     */
    public static String ILLEGAL_PROVIDER_CLASS_NAME(Object arg0) {
        return localizer.localize(localizableILLEGAL_PROVIDER_CLASS_NAME(arg0));
    }

    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("dependent.class.of.provider.format.error", arg0, arg1, arg2);
    }

    /**
     * {0}. A dependent class of the class {1} implementing the provider {2} is malformed. The provider implementation is ignored. Check if the malformed class is part of a stubbed jar that used for compiling only. 
     * 
     */
    public static String DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(arg0, arg1, arg2));
    }

    public static Localizable localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("dependent.class.of.provider.not.found", arg0, arg1, arg2);
    }

    /**
     * A dependent class, {0}, of the class {1} implementing the provider {2} is not found. The provider implementation is ignored.
     * 
     */
    public static String DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableDEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(arg0, arg1, arg2));
    }

    public static Localizable localizableURITEMPLATE_CANNOT_BE_NULL() {
        return messageFactory.getMessage("uritemplate.cannot.be.null");
    }

    /**
     * URI template cannot be null.
     * 
     */
    public static String URITEMPLATE_CANNOT_BE_NULL() {
        return localizer.localize(localizableURITEMPLATE_CANNOT_BE_NULL());
    }

    public static Localizable localizablePROVIDER_NOT_FOUND(Object arg0, Object arg1) {
        return messageFactory.getMessage("provider.not.found", arg0, arg1);
    }

    /**
     * The class {0} implementing the provider {1} is not found. The provider implementation is ignored.
     * 
     */
    public static String PROVIDER_NOT_FOUND(Object arg0, Object arg1) {
        return localizer.localize(localizablePROVIDER_NOT_FOUND(arg0, arg1));
    }

    public static Localizable localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("provider.class.could.not.be.loaded", arg0, arg1, arg2);
    }

    /**
     * The class {0} implementing provider {1} could not be loaded: {2}
     * 
     */
    public static String PROVIDER_CLASS_COULD_NOT_BE_LOADED(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizablePROVIDER_CLASS_COULD_NOT_BE_LOADED(arg0, arg1, arg2));
    }

}
