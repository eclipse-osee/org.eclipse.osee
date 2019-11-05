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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class ContextParser {

   private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

   private static final String CONTEXT = "context";
   private static final String TOPIC = "topic";
   private static final String HREF = "href";
   private static final String ID_TAG = "id";

   public Map<String, ContextEntry> entries = new HashMap<>();
   public String path;

   private String localName;
   private String uri;
   private ContextEntry currentEntry;

   public ContextParser(String path) {
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
            if (CONTEXT.equals(localName)) {

               String id = reader.getAttributeValue(uri, ID_TAG);
               if (Strings.isValid(id)) {
                  currentEntry = new ContextEntry(id);
               }

            } else if (TOPIC.equals(localName) && currentEntry != null) {
               String reference = reader.getAttributeValue(uri, HREF);
               if (Strings.isValid(reference)) {
                  String path = normalizePath(reference);
                  currentEntry.getReferences().add(path);
               }
            }
            break;
         case XMLStreamConstants.END_ELEMENT:
            localName = reader.getLocalName();
            uri = reader.getNamespaceURI();
            if (CONTEXT.equals(localName) && currentEntry != null) {
               entries.put(currentEntry.getId(), currentEntry);
               reset();
            }
            break;
      }
   }

   private String normalizePath(String reference) {
      return reference.replaceAll("\\.html#.*", ".html");
   }

   private void reset() {
      localName = null;
      uri = null;
      currentEntry = null;
   }

   public Collection<ContextEntry> getEntries() {
      return entries.values();
   }

   public ContextEntry getEntry(String id) {
      return entries.get(id);
   }

   public Set<String> getIds() {
      return entries.keySet();
   }

   public final class ContextEntry {
      private final String id;
      private final Set<String> references = new LinkedHashSet<>();

      public ContextEntry(String id) {
         super();
         this.id = id;
      }

      public String getId() {
         return id;
      }

      public Set<String> getReferences() {
         return references;
      }

   }

}
