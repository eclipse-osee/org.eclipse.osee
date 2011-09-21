
package com.sun.jersey.impl;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessageFactory;
import com.sun.jersey.localization.Localizer;


/**
 * Defines string formatting method for each constant in the resource file
 * 
 */
public final class ImplMessages {

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.jersey.impl.impl");
    private final static Localizer localizer = new Localizer();

    public static Localizable localizableERROR_NO_SUB_RES_METHOD_LOCATOR_FOUND(Object arg0) {
        return messageFactory.getMessage("error.no.sub.res.method.locator.found", arg0);
    }

    /**
     * A resource class, {0}, does not have any resource method, sub-resource method, or sub-resource locator.
     * 
     */
    public static String ERROR_NO_SUB_RES_METHOD_LOCATOR_FOUND(Object arg0) {
        return localizer.localize(localizableERROR_NO_SUB_RES_METHOD_LOCATOR_FOUND(arg0));
    }

    public static Localizable localizableQUALITY_GREATER_THAN_ONE(Object arg0) {
        return messageFactory.getMessage("quality.greater.than.one", arg0);
    }

    /**
     * Quality value "{0}" is greater than 1.
     * 
     */
    public static String QUALITY_GREATER_THAN_ONE(Object arg0) {
        return localizer.localize(localizableQUALITY_GREATER_THAN_ONE(arg0));
    }

    public static Localizable localizableAMBIGUOUS_SRMS_OUT(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return messageFactory.getMessage("ambiguous.srms.out", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    /**
     * A resource, {0}, has ambiguous sub-resource method for HTTP method {1}, URI path template {2}, and output mime-type: {3}. The problematic mime-type sets (as defined by @Produces annotation at Java methods {4} and {5}) are {6} and {7}
     * 
     */
    public static String AMBIGUOUS_SRMS_OUT(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return localizer.localize(localizableAMBIGUOUS_SRMS_OUT(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
    }

    public static Localizable localizableAMBIGUOUS_RMS_OUT(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return messageFactory.getMessage("ambiguous.rms.out", arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /**
     * A resource, {0}, has ambiguous resource method for HTTP method {1} and output mime-type: {2}. The problematic mime-type sets (as defined by @Produces annotation at Java methods {3} and {4}) are {5} and {6}
     * 
     */
    public static String AMBIGUOUS_RMS_OUT(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return localizer.localize(localizableAMBIGUOUS_RMS_OUT(arg0, arg1, arg2, arg3, arg4, arg5, arg6));
    }

    public static Localizable localizableAMBIGUOUS_RMS_IN(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return messageFactory.getMessage("ambiguous.rms.in", arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /**
     * A resource, {0}, has ambiguous resource method for HTTP method {1} and input mime-type: {2}. The problematic mime-type sets (as defined by @Consumes annotation at Java methods {3} and {4}) are {5} and {6}. This could cause an error for conflicting output types!
     * 
     */
    public static String AMBIGUOUS_RMS_IN(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
        return localizer.localize(localizableAMBIGUOUS_RMS_IN(arg0, arg1, arg2, arg3, arg4, arg5, arg6));
    }

    public static Localizable localizableDEFAULT_COULD_NOT_PROCESS_METHOD(Object arg0, Object arg1) {
        return messageFactory.getMessage("default.could.not.process.method", arg0, arg1);
    }

    /**
     * Default value, {0} could not be processed by method {1}.
     * 
     */
    public static String DEFAULT_COULD_NOT_PROCESS_METHOD(Object arg0, Object arg1) {
        return localizer.localize(localizableDEFAULT_COULD_NOT_PROCESS_METHOD(arg0, arg1));
    }

    public static Localizable localizableNESTED_ERROR(Object arg0) {
        return messageFactory.getMessage("nested.error", arg0);
    }

    /**
     * NESTED ERROR: {0}.
     * 
     */
    public static String NESTED_ERROR(Object arg0) {
        return localizer.localize(localizableNESTED_ERROR(arg0));
    }

    public static Localizable localizableAMBIGUOUS_SRMS_IN(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return messageFactory.getMessage("ambiguous.srms.in", arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    /**
     * A resource, {0}, has ambiguous sub-resource method for HTTP method {1}, URI path template {2}, and input mime-type: {3}. The problematic mime-type sets (as defined by @Consumes annotation at Java methods {4} and {5}) are {6} and {7}. This could cause an error for conflicting output types!
     * 
     */
    public static String AMBIGUOUS_SRMS_IN(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7) {
        return localizer.localize(localizableAMBIGUOUS_SRMS_IN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
    }

    public static Localizable localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(Object arg0, Object arg1) {
        return messageFactory.getMessage("sub.res.method.treated.as.res.method", arg0, arg1);
    }

    /**
     * A sub-resource method, {0}, with URI template, "{1}", is treated as a resource method
     * 
     */
    public static String SUB_RES_METHOD_TREATED_AS_RES_METHOD(Object arg0, Object arg1) {
        return localizer.localize(localizableSUB_RES_METHOD_TREATED_AS_RES_METHOD(arg0, arg1));
    }

    public static Localizable localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(Object arg0, Object arg1) {
        return messageFactory.getMessage("multiple.http.method.designators", arg0, arg1);
    }

    /**
     * A (sub-)resource method, {0}, should have only one HTTP method designator. It currently has the following designators defined: {1}
     * 
     */
    public static String MULTIPLE_HTTP_METHOD_DESIGNATORS(Object arg0, Object arg1) {
        return localizer.localize(localizableMULTIPLE_HTTP_METHOD_DESIGNATORS(arg0, arg1));
    }

    public static Localizable localizableAMBIGUOUS_CTORS(Object arg0) {
        return messageFactory.getMessage("ambiguous.ctors", arg0);
    }

    /**
     * A root resource, {0}, has ambiguous constructors to use for initialization
     * 
     */
    public static String AMBIGUOUS_CTORS(Object arg0) {
        return localizer.localize(localizableAMBIGUOUS_CTORS(arg0));
    }

    public static Localizable localizableFAILED_TO_CREATE_WEB_RESOURCE(Object arg0) {
        return messageFactory.getMessage("failed.to.create.web.resource", arg0);
    }

    /**
     * Failed to create Web resource: {0}.
     * 
     */
    public static String FAILED_TO_CREATE_WEB_RESOURCE(Object arg0) {
        return localizer.localize(localizableFAILED_TO_CREATE_WEB_RESOURCE(arg0));
    }

    public static Localizable localizableERROR_GET_RETURNS_VOID(Object arg0) {
        return messageFactory.getMessage("error.get.returns.void", arg0);
    }

    /**
     * A HTTP GET method, {0}, MUST return a non-void type.
     * 
     */
    public static String ERROR_GET_RETURNS_VOID(Object arg0) {
        return localizer.localize(localizableERROR_GET_RETURNS_VOID(arg0));
    }

    public static Localizable localizableERROR_RES_URI_PATH_INVALID(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.res.uri.path.invalid", arg0, arg1);
    }

    /**
     * A root resource class, {0}, has an invalid URI path: {1}.
     * 
     */
    public static String ERROR_RES_URI_PATH_INVALID(Object arg0, Object arg1) {
        return localizer.localize(localizableERROR_RES_URI_PATH_INVALID(arg0, arg1));
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

    public static Localizable localizableERROR_PROCESSING_METHOD(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.processing.method", arg0, arg1);
    }

    /**
     * Error processing resource method, {0}, for ResourceMethodDispatchProvider, {1}.
     * 
     */
    public static String ERROR_PROCESSING_METHOD(Object arg0, Object arg1) {
        return localizer.localize(localizableERROR_PROCESSING_METHOD(arg0, arg1));
    }

    public static Localizable localizableBAD_URITEMPLATE(Object arg0, Object arg1) {
        return messageFactory.getMessage("bad.uritemplate", arg0, arg1);
    }

    /**
     * Web resource, "{0}": URI template "{1}" of @Path could not be processed on Web resource.
     * 
     */
    public static String BAD_URITEMPLATE(Object arg0, Object arg1) {
        return localizer.localize(localizableBAD_URITEMPLATE(arg0, arg1));
    }

    public static Localizable localizableRESOURCE_NOT_ACCEPTABLE(Object arg0, Object arg1) {
        return messageFactory.getMessage("resource.not.acceptable", arg0, arg1);
    }

    /**
     * {0}, is returning a MIME type, "{1}", that is not acceptable.
     * 
     */
    public static String RESOURCE_NOT_ACCEPTABLE(Object arg0, Object arg1) {
        return localizer.localize(localizableRESOURCE_NOT_ACCEPTABLE(arg0, arg1));
    }

    public static Localizable localizableGENERIC_TYPE_NOT_SUPPORTED(Object arg0, Object arg1) {
        return messageFactory.getMessage("generic.type.not.supported", arg0, arg1);
    }

    /**
     * Generic type, {0}, not support for parameter {1}.
     * 
     */
    public static String GENERIC_TYPE_NOT_SUPPORTED(Object arg0, Object arg1) {
        return localizer.localize(localizableGENERIC_TYPE_NOT_SUPPORTED(arg0, arg1));
    }

    public static Localizable localizableBAD_CONTENT_TYPE(Object arg0) {
        return messageFactory.getMessage("bad.content.type", arg0);
    }

    /**
     * The HTTP header field "Content-Type" with value "{0}" could not be parsed.
     * 
     */
    public static String BAD_CONTENT_TYPE(Object arg0) {
        return localizer.localize(localizableBAD_CONTENT_TYPE(arg0));
    }

    public static Localizable localizableERROR_SUBRES_LOC_URI_PATH_INVALID(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.subres.loc.uri.path.invalid", arg0, arg1);
    }

    /**
     * A sub-resource locator, {0}, has an invalid URI path: {1}
     * 
     */
    public static String ERROR_SUBRES_LOC_URI_PATH_INVALID(Object arg0, Object arg1) {
        return localizer.localize(localizableERROR_SUBRES_LOC_URI_PATH_INVALID(arg0, arg1));
    }

    public static Localizable localizableNOT_VALID_HTTPMETHOD(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("not.valid.httpmethod", arg0, arg1, arg2);
    }

    /**
     * Method, {0}, annotated with {1} of resource, {2}, is not recognized as valid resource method.
     * 
     */
    public static String NOT_VALID_HTTPMETHOD(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableNOT_VALID_HTTPMETHOD(arg0, arg1, arg2));
    }

    public static Localizable localizableNON_PUB_SUB_RES_LOC(Object arg0) {
        return messageFactory.getMessage("non.pub.sub.res.loc", arg0);
    }

    /**
     * A sub-resource locator, {0}, MUST be public scoped otherwise the method is ignored
     * 
     */
    public static String NON_PUB_SUB_RES_LOC(Object arg0) {
        return localizer.localize(localizableNON_PUB_SUB_RES_LOC(arg0));
    }

    public static Localizable localizableERROR_GET_CONSUMES_ENTITY(Object arg0) {
        return messageFactory.getMessage("error.get.consumes.entity", arg0);
    }

    /**
     * A HTTP GET method, {0}, should not consume any entity.
     * 
     */
    public static String ERROR_GET_CONSUMES_ENTITY(Object arg0) {
        return localizer.localize(localizableERROR_GET_CONSUMES_ENTITY(arg0));
    }

    public static Localizable localizableWEB_APP_ALREADY_INITIATED() {
        return messageFactory.getMessage("web.app.already.initiated");
    }

    /**
     * Web application is already initiated.
     * 
     */
    public static String WEB_APP_ALREADY_INITIATED() {
        return localizer.localize(localizableWEB_APP_ALREADY_INITIATED());
    }

    public static Localizable localizableAMBIGUOUS_SRLS(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("ambiguous.srls", arg0, arg1, arg2);
    }

    /**
     * A resource, {0}, has ambiguous sub-resource locator for URI template {1}, which matches with template {2}
     * 
     */
    public static String AMBIGUOUS_SRLS(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableAMBIGUOUS_SRLS(arg0, arg1, arg2));
    }

    public static Localizable localizableERROR_UNMARSHALLING_JAXB(Object arg0) {
        return messageFactory.getMessage("error.unmarshalling.jaxb", arg0);
    }

    /**
     * Error unmarshalling JAXB object of type "{0}".
     * 
     */
    public static String ERROR_UNMARSHALLING_JAXB(Object arg0) {
        return localizer.localize(localizableERROR_UNMARSHALLING_JAXB(arg0));
    }

    public static Localizable localizableBAD_CLASS_CONSUMEMIME(Object arg0, Object arg1) {
        return messageFactory.getMessage("bad.class.consumemime", arg0, arg1);
    }

    /**
     * Web resource, "{0}": MIME types "{1}" of @ConsumeMime could not be processed on Web resource.
     * 
     */
    public static String BAD_CLASS_CONSUMEMIME(Object arg0, Object arg1) {
        return localizer.localize(localizableBAD_CLASS_CONSUMEMIME(arg0, arg1));
    }

    public static Localizable localizableERROR_SUBRES_METHOD_URI_PATH_INVALID(Object arg0, Object arg1) {
        return messageFactory.getMessage("error.subres.method.uri.path.invalid", arg0, arg1);
    }

    /**
     * A sub-resource method, {0}, has an invalid URI path: {1}
     * 
     */
    public static String ERROR_SUBRES_METHOD_URI_PATH_INVALID(Object arg0, Object arg1) {
        return localizer.localize(localizableERROR_SUBRES_METHOD_URI_PATH_INVALID(arg0, arg1));
    }

    public static Localizable localizableBAD_METHOD_CONSUMEMIME(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("bad.method.consumemime", arg0, arg1, arg2);
    }

    /**
     * Web resource, "{0}": MIME types "{1}" of @ConsumeMime could not be processed on method "{2} of Web Resource.
     * 
     */
    public static String BAD_METHOD_CONSUMEMIME(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableBAD_METHOD_CONSUMEMIME(arg0, arg1, arg2));
    }

    public static Localizable localizableILLEGAL_INITIAL_CAPACITY(Object arg0) {
        return messageFactory.getMessage("illegal.initial.capacity", arg0);
    }

    /**
     * Illegal initial capacity: {0}.
     * 
     */
    public static String ILLEGAL_INITIAL_CAPACITY(Object arg0) {
        return localizer.localize(localizableILLEGAL_INITIAL_CAPACITY(arg0));
    }

    public static Localizable localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(Object arg0) {
        return messageFactory.getMessage("new.ar.created.by.introspection.modeler", arg0);
    }

    /**
     * A new abstract resource created by IntrospectionModeler: {0}
     * 
     */
    public static String NEW_AR_CREATED_BY_INTROSPECTION_MODELER(Object arg0) {
        return localizer.localize(localizableNEW_AR_CREATED_BY_INTROSPECTION_MODELER(arg0));
    }

    public static Localizable localizableOBJECT_NOT_A_WEB_RESOURCE(Object arg0) {
        return messageFactory.getMessage("object.not.a.webResource", arg0);
    }

    /**
     * Object, "{0}": is not a Web resource since it is not annotated with @Path.
     * 
     */
    public static String OBJECT_NOT_A_WEB_RESOURCE(Object arg0) {
        return localizer.localize(localizableOBJECT_NOT_A_WEB_RESOURCE(arg0));
    }

    public static Localizable localizableAMBIGUOUS_PARAMETER(Object arg0, Object arg1) {
        return messageFactory.getMessage("ambiguous.parameter", arg0, arg1);
    }

    /**
     * Parameter {1} of {0} MUST be only one of a path, query, matrix or header parameter. 
     * 
     */
    public static String AMBIGUOUS_PARAMETER(Object arg0, Object arg1) {
        return localizer.localize(localizableAMBIGUOUS_PARAMETER(arg0, arg1));
    }

    public static Localizable localizableERROR_RES_URI_PATH_REQUIRED(Object arg0) {
        return messageFactory.getMessage("error.res.uri.path.required", arg0);
    }

    /**
     * A root resource class, {0}, MUST have a URI path.
     * 
     */
    public static String ERROR_RES_URI_PATH_REQUIRED(Object arg0) {
        return localizer.localize(localizableERROR_RES_URI_PATH_REQUIRED(arg0));
    }

    public static Localizable localizableQUALITY_MORE_THAN_THREE(Object arg0) {
        return messageFactory.getMessage("quality.more.than.three", arg0);
    }

    /**
     * Quality value "{0}" has more than 3 digits after the decimal point.
     * 
     */
    public static String QUALITY_MORE_THAN_THREE(Object arg0) {
        return localizer.localize(localizableQUALITY_MORE_THAN_THREE(arg0));
    }

    public static Localizable localizableBAD_CONSUMEMIME(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("bad.consumemime", arg0, arg1, arg2);
    }

    /**
     * Web resource, "{0}": HTTP method "{1}" of @HttpMethod could not be processed on method "{2}" of Web Resource.
     * 
     */
    public static String BAD_CONSUMEMIME(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableBAD_CONSUMEMIME(arg0, arg1, arg2));
    }

    public static Localizable localizableUNABLE_TO_WRITE_MIMEMULTIPART() {
        return messageFactory.getMessage("unable.to.write.mimemultipart");
    }

    /**
     * Unable to write MimeMultipart content.
     * 
     */
    public static String UNABLE_TO_WRITE_MIMEMULTIPART() {
        return localizer.localize(localizableUNABLE_TO_WRITE_MIMEMULTIPART());
    }

    public static Localizable localizableNON_PUB_SUB_RES_METHOD(Object arg0) {
        return messageFactory.getMessage("non.pub.sub.res.method", arg0);
    }

    /**
     * A sub-resource method, {0}, MUST be public scoped otherwise the method is ignored
     * 
     */
    public static String NON_PUB_SUB_RES_METHOD(Object arg0) {
        return localizer.localize(localizableNON_PUB_SUB_RES_METHOD(arg0));
    }

    public static Localizable localizablePROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1) {
        return messageFactory.getMessage("provider.could.not.be.created", arg0, arg1);
    }

    /**
     * Provider {0} could not be instantiated: {1}
     * 
     */
    public static String PROVIDER_COULD_NOT_BE_CREATED(Object arg0, Object arg1) {
        return localizer.localize(localizablePROVIDER_COULD_NOT_BE_CREATED(arg0, arg1));
    }

    public static Localizable localizableBAD_MIME_TYPE(Object arg0) {
        return messageFactory.getMessage("bad.mime.type", arg0);
    }

    /**
     * The MIME type of the representation with value "{0}" could not be parsed.
     * 
     */
    public static String BAD_MIME_TYPE(Object arg0) {
        return localizer.localize(localizableBAD_MIME_TYPE(arg0));
    }

    public static Localizable localizableAMBIGUOUS_RR_PATH(Object arg0, Object arg1) {
        return messageFactory.getMessage("ambiguous.rr.path", arg0, arg1);
    }

    /**
     * A root resource, {0}, has a non-unique URI template {1}
     * 
     */
    public static String AMBIGUOUS_RR_PATH(Object arg0, Object arg1) {
        return localizer.localize(localizableAMBIGUOUS_RR_PATH(arg0, arg1));
    }

    public static Localizable localizableILLEGAL_LOAD_FACTOR(Object arg0) {
        return messageFactory.getMessage("illegal.load.factor", arg0);
    }

    /**
     * Illegal load factor: {0}.
     * 
     */
    public static String ILLEGAL_LOAD_FACTOR(Object arg0) {
        return localizer.localize(localizableILLEGAL_LOAD_FACTOR(arg0));
    }

    public static Localizable localizableERROR_MARSHALLING_JAXB(Object arg0) {
        return messageFactory.getMessage("error.marshalling.jaxb", arg0);
    }

    /**
     * Error marshalling JAXB object of type "{0}".
     * 
     */
    public static String ERROR_MARSHALLING_JAXB(Object arg0) {
        return localizer.localize(localizableERROR_MARSHALLING_JAXB(arg0));
    }

    public static Localizable localizableNOT_VALID_DYNAMICRESOLVINGMETHOD(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("not.valid.dynamicresolvingmethod", arg0, arg1, arg2);
    }

    /**
     * Method, {0}, annotated with URI template {1} of resource, {2}, is not recognized as valid Java method annotated with @Path.
     * 
     */
    public static String NOT_VALID_DYNAMICRESOLVINGMETHOD(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableNOT_VALID_DYNAMICRESOLVINGMETHOD(arg0, arg1, arg2));
    }

    public static Localizable localizableRESOURCE_METHOD(Object arg0, Object arg1) {
        return messageFactory.getMessage("resource.method", arg0, arg1);
    }

    /**
     * Resource, {0}, with method, {1}
     * 
     */
    public static String RESOURCE_METHOD(Object arg0, Object arg1) {
        return localizer.localize(localizableRESOURCE_METHOD(arg0, arg1));
    }

    public static Localizable localizableNO_WEBRESOURCECLASS_IN_WEBXML() {
        return messageFactory.getMessage("no.webresourceclass.in.webxml");
    }

    /**
     * No "webresourceclass" specified in web.xml.
     * 
     */
    public static String NO_WEBRESOURCECLASS_IN_WEBXML() {
        return localizer.localize(localizableNO_WEBRESOURCECLASS_IN_WEBXML());
    }

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

    public static Localizable localizableBAD_ACCEPT_FIELD(Object arg0) {
        return messageFactory.getMessage("bad.accept.field", arg0);
    }

    /**
     * The HTTP header field "Accept" with value "{0}" could not be parsed.
     * 
     */
    public static String BAD_ACCEPT_FIELD(Object arg0) {
        return localizer.localize(localizableBAD_ACCEPT_FIELD(arg0));
    }

    public static Localizable localizableERROR_SUBRES_LOC_HAS_ENTITY_PARAM(Object arg0) {
        return messageFactory.getMessage("error.subres.loc.has.entity.param", arg0);
    }

    /**
     * A sub-resource locator, {0}, can not have an entity parameter. Try to move the parameter to the corresponding resource method.
     * 
     */
    public static String ERROR_SUBRES_LOC_HAS_ENTITY_PARAM(Object arg0) {
        return localizer.localize(localizableERROR_SUBRES_LOC_HAS_ENTITY_PARAM(arg0));
    }

    public static Localizable localizableROOT_RES_NO_PUBLIC_CTOR(Object arg0) {
        return messageFactory.getMessage("root.res.no.public.ctor", arg0);
    }

    /**
     * A root resource, {0}, MUST have a public constructor
     * 
     */
    public static String ROOT_RES_NO_PUBLIC_CTOR(Object arg0) {
        return localizer.localize(localizableROOT_RES_NO_PUBLIC_CTOR(arg0));
    }

    public static Localizable localizableRESOURCE_MIMETYPE_NOT_IN_PRODUCE_MIME(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("resource.mimetype.not.in.produceMime", arg0, arg1, arg2);
    }

    /**
     * {0}, is returning a MIME type, "{1}", that is acceptable but not a member of @ProduceMime, {2}.
     * 
     */
    public static String RESOURCE_MIMETYPE_NOT_IN_PRODUCE_MIME(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableRESOURCE_MIMETYPE_NOT_IN_PRODUCE_MIME(arg0, arg1, arg2));
    }

    public static Localizable localizableBAD_CLASS_PRODUCEMIME(Object arg0, Object arg1) {
        return messageFactory.getMessage("bad.class.producemime", arg0, arg1);
    }

    /**
     * Web resource, "{0}": MIME types "{1}" of @ProcudeMime could not be processed on Web resource.
     * 
     */
    public static String BAD_CLASS_PRODUCEMIME(Object arg0, Object arg1) {
        return localizer.localize(localizableBAD_CLASS_PRODUCEMIME(arg0, arg1));
    }

    public static Localizable localizableNON_PUB_RES_METHOD(Object arg0) {
        return messageFactory.getMessage("non.pub.res.method", arg0);
    }

    /**
     * A resource method, {0}, MUST be public scoped otherwise the method is ignored
     * 
     */
    public static String NON_PUB_RES_METHOD(Object arg0) {
        return localizer.localize(localizableNON_PUB_RES_METHOD(arg0));
    }

    public static Localizable localizableBYTE_ARRAY_CANNOT_BE_NULL() {
        return messageFactory.getMessage("byte.array.cannot.be.null");
    }

    /**
     * data parameter, of type byte[], cannot be null.
     * 
     */
    public static String BYTE_ARRAY_CANNOT_BE_NULL() {
        return localizer.localize(localizableBYTE_ARRAY_CANNOT_BE_NULL());
    }

    public static Localizable localizableBAD_METHOD_PRODUCEMIME(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("bad.method.producemime", arg0, arg1, arg2);
    }

    /**
     * Web resource, "{0}": MIME types "{1}" of @ProduceMime could not be processed on method "{2} of Web Resource.
     * 
     */
    public static String BAD_METHOD_PRODUCEMIME(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableBAD_METHOD_PRODUCEMIME(arg0, arg1, arg2));
    }

    public static Localizable localizableNO_ROOT_RES_IN_RES_CFG() {
        return messageFactory.getMessage("no.root.res.in.res.cfg");
    }

    /**
     * The ResourceConfig instance does not contain any root resource classes.
     * 
     */
    public static String NO_ROOT_RES_IN_RES_CFG() {
        return localizer.localize(localizableNO_ROOT_RES_IN_RES_CFG());
    }

    public static Localizable localizableBAD_METHOD_HTTPMETHOD(Object arg0, Object arg1, Object arg2) {
        return messageFactory.getMessage("bad.method.httpmethod", arg0, arg1, arg2);
    }

    /**
     * Web resource, "{0}": HTTP method "{1}" of @HttpMethod could not be processed on method "{2}" of Web Resource.
     * 
     */
    public static String BAD_METHOD_HTTPMETHOD(Object arg0, Object arg1, Object arg2) {
        return localizer.localize(localizableBAD_METHOD_HTTPMETHOD(arg0, arg1, arg2));
    }

    public static Localizable localizableDEFAULT_COULD_NOT_PROCESS_CONSTRUCTOR(Object arg0, Object arg1) {
        return messageFactory.getMessage("default.could.not.process.constructor", arg0, arg1);
    }

    /**
     * Default value, {0} could not be processed by constructor {1}.
     * 
     */
    public static String DEFAULT_COULD_NOT_PROCESS_CONSTRUCTOR(Object arg0, Object arg1) {
        return localizer.localize(localizableDEFAULT_COULD_NOT_PROCESS_CONSTRUCTOR(arg0, arg1));
    }

    public static Localizable localizableFATAL_ISSUES_FOUND_AT_RES_CLASS(Object arg0) {
        return messageFactory.getMessage("fatal.issues.found.at.res.class", arg0);
    }

    /**
     * Fatal issues found at class {0}. See logs for more details.
     * 
     */
    public static String FATAL_ISSUES_FOUND_AT_RES_CLASS(Object arg0) {
        return localizer.localize(localizableFATAL_ISSUES_FOUND_AT_RES_CLASS(arg0));
    }

    public static Localizable localizablePROVIDER_NOT_FOUND(Object arg0) {
        return messageFactory.getMessage("provider.not.found", arg0);
    }

    /**
     * Provider {0} not found.
     * 
     */
    public static String PROVIDER_NOT_FOUND(Object arg0) {
        return localizer.localize(localizablePROVIDER_NOT_FOUND(arg0));
    }

    public static Localizable localizableERROR_SUBRES_LOC_RETURNS_VOID(Object arg0) {
        return messageFactory.getMessage("error.subres.loc.returns.void", arg0);
    }

    /**
     * A sub-resource locator, {0}, MUST return a non-void type.
     * 
     */
    public static String ERROR_SUBRES_LOC_RETURNS_VOID(Object arg0) {
        return localizer.localize(localizableERROR_SUBRES_LOC_RETURNS_VOID(arg0));
    }

    public static Localizable localizableEXCEPTION_INVOKING_RESOURCE_METHOD() {
        return messageFactory.getMessage("exception.invoking.resource.method");
    }

    /**
     * Exception invoking Web resource method.
     * 
     */
    public static String EXCEPTION_INVOKING_RESOURCE_METHOD() {
        return localizer.localize(localizableEXCEPTION_INVOKING_RESOURCE_METHOD());
    }

}
