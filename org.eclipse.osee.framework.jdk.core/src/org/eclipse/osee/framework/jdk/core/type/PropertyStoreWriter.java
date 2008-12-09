/*
 * Created on Feb 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PropertyStoreWriter {
   private static final String TAG_SECTION = "store";
   private static final String TAG_NAME = "id";
   private static final String TAG_KEY = "key";
   private static final String TAG_VALUE = "value";
   private static final String TAG_LIST = "list";
   private static final String TAG_ITEM = "item";

   public void load(PropertyStore store, Reader reader) throws IOException, SAXException, ParserConfigurationException {
      Document document = null;
      try {
         DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         document = parser.parse(new InputSource(reader));

         //Strip out any comments first
         Node root = document.getFirstChild();
         while (root.getNodeType() == Node.COMMENT_NODE) {
            document.removeChild(root);
            root = document.getFirstChild();
         }
         load(store, document, (Element) root);
      } finally {
         if (reader != null) {
            reader.close();
         }
      }
   }

   public void load(PropertyStore store, InputStream inputStream) throws IOException, SAXException, ParserConfigurationException {
      load(store, new BufferedReader(new InputStreamReader(inputStream, "utf-8")));
   }

   public void save(PropertyStore store, OutputStream stream) throws IOException {
      XMLWriter writer = new XMLWriter(stream);
      internalSave(store, writer);
   }

   public void save(PropertyStore store, Writer writer) throws IOException {
      XMLWriter internalWriter = new XMLWriter(writer);
      internalSave(store, internalWriter);
   }

   @SuppressWarnings("unchecked")
   private void load(PropertyStore store, Document document, Element root) {
      store.setId(root.getAttribute(TAG_NAME));
      NodeList l = root.getElementsByTagName(TAG_ITEM);
      for (int i = 0; i < l.getLength(); i++) {
         Node n = l.item(i);
         if (root == n.getParentNode()) {
            String key = ((Element) l.item(i)).getAttribute(TAG_KEY);
            String value = ((Element) l.item(i)).getAttribute(TAG_VALUE);
            store.put(key, value);
         }
      }
      l = root.getElementsByTagName(TAG_LIST);
      for (int i = 0; i < l.getLength(); i++) {
         Node n = l.item(i);
         if (root == n.getParentNode()) {
            Element child = (Element) l.item(i);
            String key = child.getAttribute(TAG_KEY);
            NodeList list = child.getElementsByTagName(TAG_ITEM);
            List valueList = new ArrayList();
            for (int j = 0; j < list.getLength(); j++) {
               Element node = (Element) list.item(j);
               if (child == node.getParentNode()) {
                  valueList.add(node.getAttribute(TAG_VALUE));
               }
            }
            String[] value = new String[valueList.size()];
            valueList.toArray(value);
            store.put(key, value);
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void internalSave(PropertyStore store, XMLWriter out) {
      HashMap attributes = new HashMap(2);
      String name = store.getId();
      attributes.put(TAG_NAME, name == null ? "" : name); //$NON-NLS-1$
      out.startTag(TAG_SECTION, attributes);
      attributes.clear();
      Properties items = store.getItems();
      for (Iterator i = items.keySet().iterator(); i.hasNext();) {
         String key = (String) i.next();
         attributes.put(TAG_KEY, key == null ? "" : key); //$NON-NLS-1$
         String string = (String) items.get(key);
         attributes.put(TAG_VALUE, string == null ? "" : string); //$NON-NLS-1$        
         out.printTag(TAG_ITEM, attributes, true);
      }

      attributes.clear();
      Properties arrayItems = store.getArrays();
      for (Iterator i = arrayItems.keySet().iterator(); i.hasNext();) {
         String key = (String) i.next();
         attributes.put(TAG_KEY, key == null ? "" : key); //$NON-NLS-1$
         out.startTag(TAG_LIST, attributes);
         String[] value = (String[]) arrayItems.get(key);
         attributes.clear();
         if (value != null) {
            for (int index = 0; index < value.length; index++) {
               String string = value[index];
               attributes.put(TAG_VALUE, string == null ? "" : string); //$NON-NLS-1$
               out.printTag(TAG_ITEM, attributes, true);
            }
         }
         out.endTag(TAG_LIST);
         attributes.clear();
      }
      out.endTag(TAG_SECTION);
      out.close();
   }

   private static class XMLWriter extends PrintWriter {
      /** current number of tabs to use for ident */
      protected int tab;

      /** the xml header */
      protected static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; //$NON-NLS-1$

      public XMLWriter(Writer writer) {
         super(writer);
         tab = 0;
         println(XML_VERSION);
      }

      public XMLWriter(OutputStream output) throws UnsupportedEncodingException {
         this(new OutputStreamWriter(output, "UTF8")); //$NON-NLS-1$
      }

      /**
       * write the intended end tag
       * 
       * @param name the name of the tag to end
       */
      public void endTag(String name) {
         tab--;
         printTag("/" + name, null, false); //$NON-NLS-1$
      }

      private void printTabulation() {
         for (int i = 0; i < tab; i++) {
            super.print('\t');
         }
      }

      @SuppressWarnings("unchecked")
      public void printTag(String name, HashMap parameters, boolean close) {
         printTag(name, parameters, true, true, close);
      }

      @SuppressWarnings("unchecked")
      private void printTag(String name, HashMap parameters, boolean shouldTab, boolean newLine, boolean close) {
         StringBuffer sb = new StringBuffer();
         sb.append('<');
         sb.append(name);
         if (parameters != null) {
            for (Enumeration e = Collections.enumeration(parameters.keySet()); e.hasMoreElements();) {
               sb.append(" "); //$NON-NLS-1$
               String key = (String) e.nextElement();
               sb.append(key);
               sb.append("=\""); //$NON-NLS-1$
               sb.append(getEscaped(String.valueOf(parameters.get(key))));
               sb.append("\""); //$NON-NLS-1$
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

      @SuppressWarnings("unchecked")
      public void startTag(String name, HashMap parameters) {
         startTag(name, parameters, true);
         tab++;
      }

      @SuppressWarnings("unchecked")
      private void startTag(String name, HashMap parameters, boolean newLine) {
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
         for (int i = 0; i < s.length(); ++i) {
            appendEscapedChar(result, s.charAt(i));
         }
         return result.toString();
      }

      private static String getReplacement(char c) {
         // Encode special XML characters into the equivalent character references.
         // The first five are defined by default for all XML documents.
         // The next three (#xD, #xA, #x9) are encoded to avoid them
         // being converted to spaces on deserialization
         switch (c) {
            case '<':
               return "lt"; //$NON-NLS-1$
            case '>':
               return "gt"; //$NON-NLS-1$
            case '"':
               return "quot"; //$NON-NLS-1$
            case '\'':
               return "apos"; //$NON-NLS-1$
            case '&':
               return "amp"; //$NON-NLS-1$
            case '\r':
               return "#x0D"; //$NON-NLS-1$
            case '\n':
               return "#x0A"; //$NON-NLS-1$
            case '\u0009':
               return "#x09"; //$NON-NLS-1$
         }
         return null;
      }
   }
}
