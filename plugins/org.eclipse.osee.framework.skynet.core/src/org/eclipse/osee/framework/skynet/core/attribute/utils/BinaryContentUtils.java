/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class BinaryContentUtils {

   public static final String ATTRIBUTE_RESOURCE_PROTOCOL = "attr";
   private static final String ATTR_RESOURCES_CONTEXT = "/resources/attr/";
   private static final int ATTR_RESOURCES_CONTEXT_LENGTH = ATTR_RESOURCES_CONTEXT.length();

   private final static int MAX_NAME_SIZE = 60;

   public static String getContentType(String extension) {
      String contentType = null;
      if (Strings.isValid(extension)) {
         contentType = URLConnection.guessContentTypeFromName("dummy." + extension);
      } else {
         contentType = "application/*";
      }
      return contentType;
   }

   public static String generateFileName(Attribute<?> attribute) {
      StringBuilder builder = new StringBuilder();
      try {
         String name = attribute.getArtifact().getName();
         if (name.length() > MAX_NAME_SIZE) {
            name = name.substring(0, MAX_NAME_SIZE);
         }
         builder.append(URLEncoder.encode(name, "UTF-8"));
         builder.append(".");
      } catch (UnsupportedEncodingException ex) {
         // Do Nothing - this is not important
      }

      builder.append(getStorageName(attribute));

      String fileTypeExtension = getExtension(attribute);
      if (Strings.isValid(fileTypeExtension)) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }

   private static String getExtension(Attribute<?> attribute) {
      String fileTypeExtension = attribute.getAttributeType().getFileTypeExtension();
      if (attribute.isOfType(CoreAttributeTypes.NativeContent)) {
         fileTypeExtension = attribute.getArtifact().getSoleAttributeValue(CoreAttributeTypes.Extension, "");
      }
      return fileTypeExtension;
   }

   public static String getStorageName(Attribute<?> attribute) {
      String guid = attribute.getArtifact().getGuid();
      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guid), "Artifact has an invalid guid [%s]", guid);
      return guid;
   }

   public static String asResourcePath(String locator) {
      String toReturn = locator;
      if (Strings.isValid(toReturn)) {
         toReturn = toReturn.replaceAll("://", "/");
      }
      return toReturn;
   }

   public static String getAttributeLocation(Response response) {
      String toReturn = response.getHeaderString(HttpHeaders.LOCATION);
      if (Strings.isValid(toReturn)) {
         int index = toReturn.indexOf(ATTR_RESOURCES_CONTEXT);
         if (index > 0 && index + ATTR_RESOURCES_CONTEXT_LENGTH < toReturn.length()) {
            toReturn = String.format("attr://%s", toReturn.substring(index + ATTR_RESOURCES_CONTEXT_LENGTH));
         }
      }
      return toReturn;
   }

}