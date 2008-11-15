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

   /* (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
    */
   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      if (maxContentLength > 0 && contents.length() + length > maxContentLength) {
         return; // don't add more characters if doing so will make the content too long
      }
      contents.append(ch, start, length);
   }

   /* (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      endElementFound(uri, localName, qName);
      contents.setLength(0); // efficiently reset the StringBuilder to be empty (but preserve its capacity)
   }

   /* (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
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
   
   /* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
	 */
	public void comment(char[] ch, int start, int length) throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
	 */
	public void endCDATA() throws SAXException {
		contents.append("]]>");
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#endDTD()
	 */
	public void endDTD() throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
	 */
	public void endEntity(String name) throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
	 */
	public void startCDATA() throws SAXException {
		contents.append("<![CDATA[");
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
	 */
	public void startEntity(String name) throws SAXException {
	}
}