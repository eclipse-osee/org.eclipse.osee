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

package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * If you want to preserve CDATA sections you need to follow this pattern:<br/>
 *
 * <pre>
 * XMLReader xmlReader = XMLReaderFactory.createXMLReader();
 * xmlReader.setContentHandler(this);
 * xmlReader.setProperty(&quot;http://xml.org/sax/properties/lexical-handler&quot;, this); //This is the important part
 * </pre>
 * </p>
 *
 * @author Ryan D. Brooks
 */
public abstract class AbstractSaxHandler extends DefaultHandler implements LexicalHandler {
   // Buffer for collecting data from the "characters" SAX event.
   private final StringBuilder contents;
   private final int maxContentLength;

   protected AbstractSaxHandler() {
      this(0);
   }

   protected AbstractSaxHandler(int maxContentLength) {
      this.contents = new StringBuilder(2000);
      this.maxContentLength = maxContentLength;
   }

   @Override
   public void characters(char[] ch, int start, int length) {
      if (maxContentLength > 0 && contents.length() + length > maxContentLength) {
         return; // don't add more characters if doing so will make the content too long
      }
      contents.append(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      try {
         endElementFound(uri, localName, qName);
      } catch (Exception ex) {
         throw new SAXException(ex);
      }
      contents.setLength(0); // efficiently reset the StringBuilder to be empty (but preserve its capacity)
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      try {
         startElementFound(uri, localName, qName, attributes);
      } catch (Exception ex) {
         throw new SAXException(ex);
      }
      contents.setLength(0); // efficiently reset the StringBuilder to be empty (but preserve its capacity)
   }

   public abstract void startElementFound(String uri, String localName, String qName, Attributes attributes) throws Exception;

   public abstract void endElementFound(String uri, String localName, String qName) throws Exception;

   public String getContents() {
      return contents.toString();
   }

   public void addContentsTo(Appendable appendable) throws IOException {
      appendable.append(contents);
   }

   @Override
   public void comment(char[] ch, int start, int length) {
      //Do nothing
   }

   @Override
   public void endCDATA() {
      contents.append("]]>");
   }

   @Override
   public void endDTD() {
      //Do nothing
   }

   @Override
   public void endEntity(String name) {
      //Do nothing
   }

   @Override
   public void startCDATA() {
      contents.append("<![CDATA[");
   }

   @Override
   public void startDTD(String name, String publicId, String systemId) {
      //Do nothing
   }

   @Override
   public void startEntity(String name) {
      //Do nothing
   }
}