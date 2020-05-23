/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.define.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.xml.sax.Attributes;

public class SaxChunkCollector {

   private final Set<String> collectionElement;
   private int currentDepth = 0;
   private boolean isCollecting = false;
   private final StringBuilder builder;
   private final List<String> collectionChunks;
   private final ISaxElementHandler handler;
   private String currentLocalName;

   public SaxChunkCollector(ISaxElementHandler handler, String... collect) {
      this.collectionElement = new HashSet<>();
      for (String item : collect) {
         collectionElement.add(item);
      }
      this.handler = handler;
      collectionChunks = new ArrayList<>();
      builder = new StringBuilder(5000);
   }

   public void endElementFound(String localName, ICollectionSource collectionSource) {
      if (isCollecting) {
         currentDepth--;
         builder.append(collectionSource.getContents());
         builder.append("</");
         builder.append(localName);
         builder.append(">");
         if (currentDepth < 1) {
            isCollecting = false;
            String data = builder.toString();
            collectionChunks.add(data);
            handler.processSaxChunkCollectorData(currentLocalName, data);
            builder.setLength(0);
         }
      }
   }

   public ISaxElementHandler getHandler() {
      return handler;
   }

   public void startElementFound(String localName, Attributes attributes, CollectionParser collectionParser) {
      if (collectionElement.contains(localName)) {
         isCollecting = true;
         currentLocalName = localName;
      }

      if (isCollecting) {
         currentDepth++;
         builder.append("<");
         builder.append(localName);
         for (int i = 0; i < attributes.getLength(); i++) {
            builder.append(" ");
            builder.append(attributes.getLocalName(i));
            builder.append("=\"");
            builder.append(attributes.getValue(i));
            builder.append("\"");
         }
         builder.append(" >");
      }
   }
}
