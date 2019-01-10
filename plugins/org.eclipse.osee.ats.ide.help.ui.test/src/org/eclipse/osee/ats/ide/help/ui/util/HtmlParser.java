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
package org.eclipse.osee.ats.ide.help.ui.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class HtmlParser {

   private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

   private static final String LINK_NODE = "link";
   private static final String HREF_TAG = "href";
   private static final String SRC_TAG = "src";

   private final String pathHint;

   public HtmlParser(String pathHint) {
      this.pathHint = pathHint;
   }

   private String getPath(String fullPath) {
      StringBuilder builder = new StringBuilder();

      String[] parts = fullPath.split("/");
      boolean found = false;
      for (String part : parts) {
         if (found && !part.endsWith(".html")) {
            builder.append(part);
            builder.append("/");
         }
         if (pathHint.equals(part)) {
            found = true;
         }
      }
      return builder.toString();
   }

   public Set<String> parse(URL url) throws Exception {
      Set<String> entries = new HashSet<>();
      entries.clear();

      String pathPrefix = getPath(url.toString());

      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(url.openStream());
         XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(inputStream);
         while (streamReader.hasNext()) {
            process(streamReader, pathPrefix, entries);
            streamReader.next();
         }

      } finally {
         Lib.close(inputStream);
      }

      return entries;
   }

   private void process(XMLStreamReader reader, String pathPrefix, Set<String> entries) {
      int eventType = reader.getEventType();
      switch (eventType) {
         case XMLStreamConstants.START_ELEMENT:
            String localName = reader.getLocalName();
            for (int index = 0; index < reader.getAttributeCount(); index++) {

               String attributeName = reader.getAttributeLocalName(index);
               String value = reader.getAttributeValue(index);

               if (Strings.isValid(value)) {
                  if (!LINK_NODE.equals(localName)) {
                     if (HREF_TAG.equals(attributeName) || SRC_TAG.equals(attributeName)) {
                        processResource(pathPrefix, entries, value);
                     }
                  }
               }
            }
            break;
      }
   }

   private void processResource(String pathPrefix, Set<String> references, String value) {
      if (!isExternalLink(value)) {
         String reference = normalizePath(pathPrefix, value);
         references.add(reference);
      }
   }

   private String normalizePath(String pathPrefix, String reference) {
      String path = reference.replaceAll("\\.html#.*", ".html");
      return String.format("%s%s", pathPrefix, path);
   }

   private boolean isExternalLink(String resource) {
      return resource.contains("://");
   }
}
