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
package org.eclipse.osee.ote.define.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.xml.sax.Attributes;

public class SaxChunkCollector {

   private Set<String> collectionElement;
   private int currentDepth = 0;
   private boolean isCollecting = false;
   private StringBuilder builder;
   private List<String> collectionChunks;
   private ISaxElementHandler handler;
   private String currentLocalName;

   public SaxChunkCollector(ISaxElementHandler handler, String... collect) {
      this.collectionElement = new HashSet<String>();
      for (String item : collect) {
         collectionElement.add(item);
      }
      this.handler = handler;
      collectionChunks = new ArrayList<String>();
      builder = new StringBuilder(5000);
   }

   /**
    * @param localName
    * @param collectionParser
    */
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

   /**
    * @param localName
    * @param attributes
    * @param collectionParser
    */
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
