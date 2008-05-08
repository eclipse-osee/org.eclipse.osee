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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;

/**
 * @author Roberto E. Escobar
 */
public class BinaryContentUtils {

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

   public static String generateFileName(DynamicAttributeManager attributeManager) {
      AttributeType attributeDescriptor = attributeManager.getAttributeType();
      StringBuilder builder = new StringBuilder();
      try {
         builder.append(URLEncoder.encode(attributeDescriptor.getName(), "UTF-8"));
         builder.append(".");
      } catch (Exception ex) {
         // Do Nothing - this is not important
      }
      builder.append(attributeManager.getParentArtifact().getGuid());

      String fileTypeExtension = attributeDescriptor.getFileTypeExtension();
      if (Strings.isValid(fileTypeExtension)) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }

}
