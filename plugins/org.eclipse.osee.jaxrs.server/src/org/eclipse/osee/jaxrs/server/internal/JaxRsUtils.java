/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.eclipse.osee.framework.jdk.core.type.ClassBasedResourceToken;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsUtils {

   private JaxRsUtils() {
      // Utility class
   }

   public static final String UTF_8_ENCODING = "UTF-8";
   public static final List<MediaType> HTML_MEDIA_TYPES = Collections.unmodifiableList(Arrays.asList(
      MediaType.APPLICATION_XHTML_XML_TYPE, MediaType.TEXT_HTML_TYPE));

   public static boolean isHtmlSupported(List<MediaType> acceptableMediaTypes) {
      return !JAXRSUtils.intersectMimeTypes(acceptableMediaTypes, HTML_MEDIA_TYPES, false).isEmpty();
   }

   public static String asTemplateValue(String value) {
      String toReturn = "N/A";
      if (Strings.isValid(value)) {
         toReturn = value.trim();
         toReturn = toReturn.replaceAll("\r?\n", "<br/>");
      }
      return toReturn;
   }

   public static ResourceToken newTemplate(String resource, Class<?> clazz) {
      return new ClassBasedResourceToken(resource, clazz);
   }

   public static IResourceRegistry newSingleTemplateRegistry(ResourceToken token) {
      IResourceRegistry registry = new ResourceRegistry();
      registry.registerResource(-1L, token);
      return registry;
   }

   public static String getComponentName(ServiceReference<?> reference) {
      return (String) reference.getProperty("component.name");
   }

   public static void checkValid(Application application) throws Exception {
      if (application == null) {
         throw new IllegalStateException("javax.ws.rs.Application service was null");
      }
      if (!hasResources(application)) {
         throw new IllegalStateException("javax.ws.rs.Application had no resources declared");
      }
   }

   public static String getApplicationPath(String componentName, Application application) {
      String applicationPath = getApplicationPath(application);
      return Strings.isValid(applicationPath) ? applicationPath : normalize(componentName);
   }

   public static String getApplicationPath(Application application) {
      String toReturn = null;
      Class<? extends Application> clazz = application.getClass();
      ApplicationPath applicationPath = clazz.getAnnotation(ApplicationPath.class);
      if (applicationPath != null) {
         toReturn = JaxRsUtils.normalize(applicationPath.value());
      }
      return toReturn;
   }

   public static boolean hasPath(String value) {
      String toCheck = value;
      if (toCheck != null) {
         toCheck = toCheck.trim();
      }
      if (toCheck.startsWith("/")) {
         toCheck = toCheck.substring(1);
      }
      return Strings.isValid(toCheck);
   }

   private static boolean hasResources(Application app) {
      return !isNullOrEmpty(app.getClasses()) || !isNullOrEmpty(app.getSingletons());
   }

   public static String normalize(String contextName) {
      return contextName != null && !contextName.startsWith("/") ? "/" + contextName : contextName;
   }

   private static boolean isNullOrEmpty(Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   public static long getLong(Map<String, Object> props, String key, long defaultValue) {
      long toReturn = defaultValue;
      String value = get(props, key, null);
      if (value != null && Strings.isNumeric(value)) {
         toReturn = Long.parseLong(value);
      }
      return toReturn;
   }

   public static boolean getBoolean(Map<String, Object> props, String key, boolean defaultValue) {
      boolean toReturn = defaultValue;
      String value = get(props, key, null);
      if (value != null) {
         toReturn = Boolean.parseBoolean(value);
      }
      return toReturn;
   }

   public static String get(Map<String, Object> props, String key, String defaultValue) {
      String toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         toReturn = String.valueOf(object);
      }
      return toReturn;
   }

   public static StatusType newStatusType(final int code, final Family family, final String reason) {
      return new StatusType() {

         @Override
         public int getStatusCode() {
            return code;
         }

         @Override
         public Family getFamily() {
            return family;
         }

         @Override
         public String getReasonPhrase() {
            return reason;
         }

      };
   }
}
