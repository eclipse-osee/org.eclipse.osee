/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.help.ui.util;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class TocParser {

   private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

   private static final String TOC = "toc";
   private static final String TOPIC = "topic";
   private static final String HREF = "href";

   public Set<String> entries = new HashSet<>();
   public String path;

   private String localName;
   private String uri;

   public TocParser(String path) {
      this.path = path;
   }

   public void parse() throws Exception {
      entries.clear();

      try (InputStream inputStream = OsgiUtil.getResourceAsStream(getClass(), path)) {
         XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(inputStream);
         while (streamReader.hasNext()) {
            process(streamReader);
            streamReader.next();
         }
      }
   }

   private void process(XMLStreamReader reader) {
      int eventType = reader.getEventType();
      switch (eventType) {
         case XMLStreamConstants.START_ELEMENT:
            localName = reader.getLocalName();
            uri = reader.getNamespaceURI();
            if (TOC.equals(localName)) {
               processReference(reader, TOPIC);
            } else if (TOPIC.equals(localName)) {
               processReference(reader, HREF);
            }
            break;
         case XMLStreamConstants.END_ELEMENT:
            localName = reader.getLocalName();
            uri = reader.getNamespaceURI();
            reset();
            break;
      }
   }

   private void processReference(XMLStreamReader reader, String tag) {
      String reference = reader.getAttributeValue(uri, tag);
      if (Strings.isValid(reference)) {
         String path = normalizePath(reference);
         entries.add(path);
      }
   }

   private String normalizePath(String reference) {
      return reference.replaceAll("\\.html#.*", ".html");
   }

   private void reset() {
      localName = null;
      uri = null;
   }

   public Set<String> getEntries() {
      return entries;
   }
}
