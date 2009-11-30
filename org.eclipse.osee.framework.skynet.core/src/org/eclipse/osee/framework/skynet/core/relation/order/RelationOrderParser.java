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
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderParser {

   private static final Object ROOT_ELEMENT = "OrderList";
   private static XMLReader xmlReader;

   public RelationOrderParser() {
   }

   public void loadFromXml(RelationOrderData data, String value) throws OseeCoreException {
      if (data == null) {
         throw new OseeArgumentException("RelationOrderData object cannot be null");
      }
      data.clear();
      if (value != null && value.trim().length() > 0) {
         try {
            if (xmlReader == null) {
               xmlReader = XMLReaderFactory.createXMLReader();
            }
            xmlReader.setContentHandler(new RelationOrderSaxHandlerLite(data));
            xmlReader.parse(new InputSource(new StringReader(value)));
         } catch (SAXException ex) {
            throw new OseeWrappedException(ex);
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         }
      }
   }

   public String toXml(RelationOrderData data) throws OseeArgumentException {
      if (data == null) {
         throw new OseeArgumentException("RelationOrderData object cannot be null");
      }

      StringBuilder sb = new StringBuilder();
      openRoot(sb);
      for (Entry<Pair<String, String>, Pair<String, List<String>>> entry : data.entrySet()) {
         writeEntry(sb, entry);
         sb.append("\n");
      }
      closeRoot(sb);
      return sb.toString();
   }

   private void openRoot(StringBuilder sb) {
      sb.append("<");
      sb.append(ROOT_ELEMENT);
      sb.append(">\n");
   }

   private void closeRoot(StringBuilder sb) {
      sb.append("</");
      sb.append(ROOT_ELEMENT);
      sb.append(">");
   }

   private void writeEntry(StringBuilder sb, Entry<Pair<String, String>, Pair<String, List<String>>> entry) {
      Pair<String, String> key = entry.getKey();
      sb.append("<");
      sb.append("Order ");
      sb.append("relType=\"");
      sb.append(key.getFirst());
      sb.append("\" side=\"");
      sb.append(key.getSecond());
      sb.append("\" orderType=\"");

      Pair<String, List<String>> value = entry.getValue();
      sb.append(value.getFirst());
      List<String> guids = value.getSecond();
      if (guids != null) {
         if (guids.size() > 0) {
            sb.append("\" list=\"");
         }
         for (int i = 0; i < guids.size(); i++) {
            sb.append(guids.get(i));
            if (i + 1 < guids.size()) {
               sb.append(",");
            }
         }
      }
      sb.append("\"");
      sb.append("/>");
   }

   private final static class RelationOrderSaxHandlerLite extends AbstractSaxHandler {
      private final RelationOrderData data;

      private RelationOrderSaxHandlerLite(RelationOrderData data) {
         this.data = data;
      }

      @Override
      public void endElementFound(String uri, String localName, String qName) throws SAXException {
      }

      @Override
      public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if ("Order".equals(localName)) {
            String relationType = attributes.getValue("relType");
            String relationSide = attributes.getValue("side");
            String orderType = attributes.getValue("orderType");
            String list = attributes.getValue("list");
            if (relationType != null && orderType != null && relationSide != null) {
               List<String> guidsList = Collections.emptyList();
               if (list != null) {
                  String[] guids = list.split(",");
                  guidsList = Arrays.asList(guids);
               }
               data.addOrderList(relationType, relationSide, orderType, guidsList);
            }
         }
      }
   }
}
