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

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class BinaryContentUtils {

   private final static int MAX_NAME_SIZE = 60;

   private BinaryContentUtils() {
   }

   public static String getContentType(String extension) {
      String contentType = null;
      if (Strings.isValid(extension)) {
         contentType = HttpURLConnection.guessContentTypeFromName("dummy." + extension);
      } else {
         contentType = "application/*";
      }
      return contentType;
   }

   public static String generateFileName(Attribute<?> attribute) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      try {
         String name = attribute.getArtifact().getDescriptiveName();
         if (name.length() > MAX_NAME_SIZE) {
            name = name.substring(0, MAX_NAME_SIZE);
         }
         builder.append(URLEncoder.encode(name, "UTF-8"));
         builder.append(".");
      } catch (Exception ex) {
         // Do Nothing - this is not important
      }
      builder.append(attribute.getArtifact().getHumanReadableId());

      String fileTypeExtension = getExtension(attribute);
      if (Strings.isValid(fileTypeExtension)) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }

   private static String getExtension(Attribute<?> attribute) throws OseeCoreException {
      String fileTypeExtension = attribute.getAttributeType().getFileTypeExtension();
      if (attribute.isOfType("Native Content")) {
         fileTypeExtension = attribute.getArtifact().getSoleAttributeValue("Extension");
      }
      return fileTypeExtension;
   }
}