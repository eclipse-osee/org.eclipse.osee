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
package org.eclipse.osee.orcs.db.internal.util;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ReadableAttribute;

/**
 * @author Roberto E. Escobar
 */
public class BinaryContentUtils {

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

   public static String generateFileName(NamedIdentity<String> identity, String fileTypeExtension) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      try {
         String name = identity.getName();
         if (name.length() > MAX_NAME_SIZE) {
            name = name.substring(0, MAX_NAME_SIZE);
         }
         builder.append(URLEncoder.encode(name, "UTF-8"));
         builder.append(".");
      } catch (UnsupportedEncodingException ex) {
         // Do Nothing - this is not important
      }

      builder.append(getStorageName(identity));

      if (Strings.isValid(fileTypeExtension)) {
         builder.append(".");
         builder.append(fileTypeExtension);
      }
      return builder.toString();
   }

   private static String getStorageName(Identity<String> identity) throws OseeCoreException {
      String guid = identity.getGuid();
      Conditions.checkExpressionFailOnTrue(!GUID.isValid(guid), "Item has an invalid guid [%s]", guid);
      return guid;
   }

   private static String getExtension(ReadableAttribute<String> attribute) throws OseeCoreException {
      AttributeType attributeType = (AttributeType) attribute.getAttributeType();

      String fileTypeExtension = null;
      if (attributeType.equals(CoreAttributeTypes.NativeContent)) {
         fileTypeExtension = attribute.getValue();
      }
      if (!Strings.isValid(fileTypeExtension)) {
         fileTypeExtension = attributeType.getFileTypeExtension();
      }
      return fileTypeExtension;
   }

}