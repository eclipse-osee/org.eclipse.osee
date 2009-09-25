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
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrderStore {

   private static final Object ROOT_ELEMENT = "OrderList";

   private final CompositeKeyHashMap<String, String, Pair<String, List<String>>> lists;

   public RelationOrderStore(String value) throws OseeWrappedException {
      lists = new CompositeKeyHashMap<String, String, Pair<String, List<String>>>();
      try {
         if (value.trim().length() > 0) {
            parseXml(value);
         }
      } catch (SAXException ex) {
         throw new OseeWrappedException(ex);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public RelationOrderStore(Artifact artifact) throws OseeCoreException {
      this(artifact.getSoleAttributeValueAsString(CoreAttributes.RELATION_ORDER.getName(), ""));
   }

   private void parseXml(String value) throws SAXException, IOException {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      RelationOrderSaxHandlerLite handler = new RelationOrderSaxHandlerLite();
      xmlReader.setContentHandler(handler);
      xmlReader.parse(new InputSource(new StringReader(value)));
   }

   public String findRelationOrderGuid(String typeName, RelationSide side) {
      Pair<String, List<String>> pair = lists.get(typeName, side.name());
      if (pair != null) {
         return pair.getFirst();
      }
      return null;
   }

   public List<String> findOrderList(String typeName, RelationSide side, String orderTypeGuid) {
      Pair<String, List<String>> pair = lists.get(typeName, side.name());
      if (pair != null) {
         return pair.getSecond();
      }
      return null;
   }

   public String getAsXmlString() {
      StringBuilder sb = new StringBuilder();
      openRoot(sb);
      for (Entry<Pair<String, String>, Pair<String, List<String>>> entry : lists.entrySet()) {
         writeEntry(sb, entry);
         sb.append("\n");
      }
      closeRoot(sb);
      return sb.toString();
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

   public void putOrderList(String typeName, RelationOrderId orderId, RelationSide side, List<String> guidList) {
      lists.put(typeName, side.name(), new Pair<String, List<String>>(orderId.getGuid(), guidList));
   }

   public void removeOrder(String typeName, RelationOrderId orderId, RelationSide side) {
      lists.remove(typeName, side.name());
   }

   public boolean hasEntries() {
      return lists.size() > 0;
   }

   private class RelationOrderSaxHandlerLite extends AbstractSaxHandler {

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
               List<String> guidsList = null;
               if (list != null) {
                  String[] guids = list.split(",");
                  guidsList = Arrays.asList(guids);
               }
               lists.put(relationType, relationSide, new Pair<String, List<String>>(orderType, guidsList));
            }
         }
      }
   }

   public String getOrderGuid(String typeName, RelationSide side) {
      Pair<String, List<String>> pair = lists.get(typeName, side.name());
      if (pair != null) {
         return pair.getFirst();
      }
      return null;
   }
}
