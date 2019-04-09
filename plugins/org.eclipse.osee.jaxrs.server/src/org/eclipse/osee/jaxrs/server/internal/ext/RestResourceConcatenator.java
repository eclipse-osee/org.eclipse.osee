/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.ext;

import com.google.common.io.ByteSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author David W. Miller
 */
public class RestResourceConcatenator {
   private final Set<String> resources = new HashSet<>();
   private String header;
   private String footer;
   private Pattern resourcePattern = null;

   public void initialize(String xmlTag) {
      if (resourcePattern != null) {
         throw new OseeArgumentException("Already initialized");
      }
      validateTag(xmlTag);
      String regex = String.format("(.*?<%s.*?>)(.+?)(</%s>)", xmlTag, xmlTag);

      resourcePattern = Pattern.compile(regex, Pattern.DOTALL);
   }

   @Override
   public String toString() {
      return String.format("%s Object: \n  contents {%s}\n", this.getClass().getName(), getResources());
   }

   public void addResource(ByteSource supplier) throws IOException {
      Conditions.checkNotNull(supplier, "InputStreamSupplier");
      InputStream is = null;
      try {
         is = supplier.openStream();
         processResource(Lib.inputStreamToString(is));
      } finally {
         Lib.close(is);
      }
   }

   public InputStream getAsInputStream() throws UnsupportedEncodingException {
      if (resources.isEmpty() || header == null || footer == null) {
         throw new OseeCoreException("Rest resource not ready - no data");
      }
      return Lib.stringToInputStream(getResources());
   }

   public String getResources() {
      StringBuilder sb = new StringBuilder();
      sb.append(header);
      for (String resource : resources) {
         sb.append(resource);
      }
      sb.append(footer);
      return sb.toString();
   }

   private void processResource(String input) {
      Conditions.checkNotNullOrEmpty(input, "bundle resource");
      Matcher match = resourcePattern.matcher(input);
      while (match.find()) {
         if (resources.isEmpty()) {
            header = match.group(1);
            footer = match.group(3);
         }
         resources.add(match.group(2));
      }
   }

   private void validateTag(String xmlTag) {

      Conditions.checkNotNullOrEmpty(xmlTag, "xmlTag");
      // resources can only have three tags: "resourceDoc", "applicationDocs" or "grammars"
      boolean valid = false;
      if (xmlTag.equals("resourceDoc")) {
         valid = true;
      } else if (xmlTag.equals("applicationDocs")) {
         valid = true;
      } else if (xmlTag.equals("grammars")) {
         valid = true;
      }
      if (!valid) {
         throw new OseeArgumentException("Invalid resource document tag");
      }
   }
}
