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
package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Ryan D. Brooks
 * 
 * If you want to preserve CDATA sections you need to follow this pattern:
 * 
 * XMLReader xmlReader = XMLReaderFactory.createXMLReader();
 * xmlReader.setContentHandler(this);
 * xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", this); //This is the important part
 * 
 */
public abstract class AbstractSaxHandler extends DefaultHandler implements LexicalHandler{
// Buffer for collecting data from the "characters" SAX event.
   private StringBuilder contents;
   private final int maxContentLength;

   protected AbstractSaxHandler() {
      this(0);
   }

   protected AbstractSaxHandler(int maxContentLength) {
      this.contents = new StringBuilder(2000);
      this.maxContentLength = maxContentLength;
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      if (maxContentLength > 0 && contents.length() + length > maxContentLength) {
         return; // don't add more characters if doing so will make the content too long
      }
      contents.append(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      endElementFound(uri, localName, qName);
      contents.setLength(0); // efficiently reset the StringBuilder to be empty (but preserve its capacity)
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      startElementFound(uri, localName, qName, attributes);
      contents.setLength(0); // efficiently reset the StringBuilder to be empty (but preserve its capacity)
   }

   public abstract void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException;

   public abstract void endElementFound(String uri, String localName, String qName) throws SAXException;

   public String getContents() {
      return contents.toString();
   }

   public void addContentsTo(Appendable appendable) throws IOException {
      appendable.append(contents);
   }
   
	public void comment(char[] ch, int start, int length) throws SAXException {
	}

	public void endCDATA() throws SAXException {
		contents.append("]]>");
	}

	public void endDTD() throws SAXException {
	}

	public void endEntity(String name) throws SAXException {
	}

	public void startCDATA() throws SAXException {
		contents.append("<![CDATA[");
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
	}

	public void startEntity(String name) throws SAXException {
	}
}