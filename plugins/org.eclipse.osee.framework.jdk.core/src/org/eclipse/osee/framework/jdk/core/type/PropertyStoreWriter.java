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
package org.eclipse.osee.framework.jdk.core.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStoreWriter {
   private static final String TAG_SECTION = "store";
   private static final String TAG_NAME = "id";
   private static final String TAG_KEY = "key";
   private static final String TAG_VALUE = "value";
   private static final String TAG_LIST = "list";
   private static final String TAG_ITEM = "item";
   private static final String TAG_INNER = "inner.store";

   public void load(PropertyStore store, Reader reader) throws Exception {
      XMLReader xmlReader = new XMLReader();
      xmlReader.load(store, reader);
   }

   public void load(PropertyStore store, InputStream inputStream) throws Exception {
      load(store, new BufferedReader(new InputStreamReader(inputStream, "utf-8")));
   }

   public void save(PropertyStore store, OutputStream stream) throws IOException {
      XMLWriter writer = new XMLWriter(stream);
      internalSave(store, writer);
      writer.close();
   }

   public void save(PropertyStore store, Writer writer) {
      XMLWriter internalWriter = new XMLWriter(writer);
      internalSave(store, internalWriter);
   }

   private void internalSave(PropertyStore store, XMLWriter out) {
      Map<String, String> attributes = new HashMap<>(2);
      String name = store.getId();
      attributes.put(TAG_NAME, name == null ? "" : name);
      out.startTag(TAG_SECTION, attributes);
      attributes.clear();

      Map<String, Object> items = store.getItems();
      for (Entry<String, Object> entry : items.entrySet()) {
         String key = entry.getKey();
         attributes.put(TAG_KEY, key == null ? "" : key);
         String value = (String) entry.getValue();
         attributes.put(TAG_VALUE, value == null ? "" : value);
         out.printTag(TAG_ITEM, attributes, true);
      }

      attributes.clear();
      Map<String, Object> arrayItems = store.getArrays();
      for (Entry<String, Object> entry : arrayItems.entrySet()) {
         String key = entry.getKey();
         attributes.put(TAG_KEY, key == null ? "" : key);
         out.startTag(TAG_LIST, attributes);

         String[] value = (String[]) entry.getValue();
         attributes.clear();
         if (value != null) {
            for (int index = 0; index < value.length; index++) {
               String item = value[index];
               attributes.put(TAG_VALUE, item == null ? "" : item);
               out.printTag(TAG_ITEM, attributes, true);
            }
         }
         out.endTag(TAG_LIST);
         attributes.clear();
      }
      attributes.clear();

      processInnerStores(store, out);

      out.endTag(TAG_SECTION);
   }

   private void processInnerStores(PropertyStore store, XMLWriter out) {
      Map<String, String> attributes = new HashMap<>(2);
      Map<String, Object> properties = store.getPropertyStores();
      for (Entry<String, Object> entry : properties.entrySet()) {
         String key = entry.getKey();
         attributes.put(TAG_KEY, key == null ? "" : key);
         out.startTag(TAG_INNER, attributes);
         PropertyStore innerStore = (PropertyStore) entry.getValue();
         internalSave(innerStore, out);
         out.endTag(TAG_INNER);
      }
   }

   private static class XMLReader {
      private static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      private List<String> valueList;
      private String tagListKey;
      private boolean isInTagList;
      private final Stack<Pair<String, PropertyStore>> innerStoreStack;
      private String name;
      private String uri;

      public XMLReader() {
         isInTagList = false;
         valueList = null;
         tagListKey = null;
         innerStoreStack = new Stack<>();
      }

      private boolean isInNestedStore() {
         return !innerStoreStack.isEmpty();
      }

      private PropertyStore getCurrentInnerStore() {
         return innerStoreStack.peek().getSecond();
      }

      public void load(PropertyStore store, Reader input) throws Exception {
         try {
            XMLStreamReader streamReader = inputFactory.createXMLStreamReader(input);
            while (streamReader.hasNext()) {
               process(store, streamReader);
               streamReader.next();
            }
         } finally {
            if (input != null) {
               input.close();
            }
         }
      }

      private void process(PropertyStore store, XMLStreamReader reader) {

         int eventType = reader.getEventType();
         switch (eventType) {
            case XMLStreamConstants.START_ELEMENT:
               name = reader.getLocalName();
               uri = reader.getNamespaceURI();
               if (TAG_SECTION.equals(name)) {
                  if (isInNestedStore()) {
                     getCurrentInnerStore().setId(reader.getAttributeValue(uri, TAG_NAME));
                  } else {
                     store.setId(reader.getAttributeValue(uri, TAG_NAME));
                  }
               } else if (TAG_ITEM.equals(name)) {
                  if (isInNestedStore()) {
                     processTagItemSection(uri, getCurrentInnerStore(), reader);
                  } else {
                     processTagItemSection(uri, store, reader);
                  }
               } else if (TAG_LIST.equals(name)) {
                  isInTagList = true;
                  tagListKey = reader.getAttributeValue(uri, TAG_KEY);
               } else if (TAG_INNER.equals(name)) {
                  String key = reader.getAttributeValue(uri, TAG_KEY);
                  innerStoreStack.add(new Pair<String, PropertyStore>(key, new PropertyStore()));
               }
               break;
            case XMLStreamConstants.END_ELEMENT:
               name = reader.getLocalName();
               uri = reader.getNamespaceURI();
               if (TAG_LIST.equals(name)) {
                  isInTagList = false;
                  if (Strings.isValid(tagListKey) && valueList != null && !valueList.isEmpty()) {
                     String[] value = valueList.toArray(new String[valueList.size()]);
                     if (isInNestedStore()) {
                        getCurrentInnerStore().put(tagListKey, value);
                     } else {
                        store.put(tagListKey, value);
                     }
                  }
                  valueList = null;
                  tagListKey = null;
               } else if (TAG_INNER.equals(name)) {
                  Pair<String, PropertyStore> completedPair = innerStoreStack.pop();
                  String completedKey = completedPair.getFirst();
                  PropertyStore completedStore = completedPair.getSecond();
                  if (isInNestedStore()) {
                     getCurrentInnerStore().put(completedKey, completedStore);
                  } else {
                     store.put(completedKey, completedStore);
                  }
               }
               break;
            case XMLStreamConstants.ENTITY_REFERENCE:
               name = reader.getLocalName();
               uri = reader.getNamespaceURI();
               break;
            default:
               break;
         }
      }

      private void processTagItemSection(String uri, PropertyStore store, XMLStreamReader reader) {
         String value = reader.getAttributeValue(uri, TAG_VALUE);
         if (isInTagList) {
            if (valueList == null) {
               valueList = new ArrayList<>();
            }
            valueList.add(value);
         } else {
            String key = reader.getAttributeValue(uri, TAG_KEY);
            if (Strings.isValid(key)) {
               store.put(key, value);
            }
         }
      }
   }

   private static class XMLWriter extends PrintWriter {
      private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$
      private int tab;

      public XMLWriter(Writer writer) {
         super(writer);
         tab = 0;
         println(XML_VERSION);
      }

      public XMLWriter(OutputStream output) throws UnsupportedEncodingException {
         this(new OutputStreamWriter(output, "UTF8"));
      }

      public void endTag(String name) {
         tab--;
         printTag("/" + name, null, false);
      }

      private void printTabulation() {
         for (int i = 0; i < tab; i++) {
            super.print('\t');
         }
      }

      public void printTag(String name, Map<String, String> parameters, boolean close) {
         printTag(name, parameters, false, false, close);
      }

      private void printTag(String name, Map<String, String> parameters, boolean shouldTab, boolean newLine, boolean close) {
         StringBuffer sb = new StringBuffer();
         sb.append('<');
         sb.append(name);
         if (parameters != null) {
            for (Enumeration<String> e = Collections.enumeration(parameters.keySet()); e.hasMoreElements();) {
               sb.append(" ");
               String key = e.nextElement();
               sb.append(key);
               sb.append("=\"");
               sb.append(getEscaped(String.valueOf(parameters.get(key))));
               sb.append("\"");
            }
         }
         if (close) {
            sb.append('/');
         }
         sb.append('>');
         if (shouldTab) {
            printTabulation();
         }
         if (newLine) {
            println(sb.toString());
         } else {
            print(sb.toString());
         }
      }

      public void startTag(String name, Map<String, String> parameters) {
         startTag(name, parameters, true);
         tab++;
      }

      private void startTag(String name, Map<String, String> parameters, boolean newLine) {
         printTag(name, parameters, true, newLine, false);
      }

      private static void appendEscapedChar(StringBuffer buffer, char c) {
         String replacement = getReplacement(c);
         if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
         } else {
            buffer.append(c);
         }
      }

      private static String getEscaped(String s) {
         StringBuffer result = new StringBuffer(s.length() + 10);
         for (int i = 0; i < s.length(); i++) {
            appendEscapedChar(result, s.charAt(i));
         }
         return result.toString();
      }

      private static String getReplacement(char character) {
         // Encode special XML characters into the equivalent character references.
         // The first five are defined by default for all XML documents.
         // The next three (#xD, #xA, #x9) are encoded to avoid them
         // being converted to spaces on deserialization
         switch (character) {
            case '<':
               return "lt";
            case '>':
               return "gt";
            case '"':
               return "quot";
            case '\'':
               return "apos";
            case '&':
               return "amp";
            case '\r':
               return "#x0D";
            case '\n':
               return "#x0A";
            case '\u0009':
               return "#x09";
         }
         return null;
      }
   }
}
