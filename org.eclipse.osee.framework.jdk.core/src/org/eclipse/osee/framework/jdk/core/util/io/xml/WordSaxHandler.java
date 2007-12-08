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

/**
 * @author Ryan D. Brooks
 */
public class WordSaxHandler extends AbstractSaxHandler {
   private boolean inHeader;
   private boolean inFooter;
   private StringBuilder headerText;
   private StringBuilder footerText;

   public WordSaxHandler() {
      super();
      headerText = new StringBuilder();
      footerText = new StringBuilder();
   }

   public void reset() {
      headerText.delete(0, 99999999);
      footerText.delete(0, 99999999);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#startElementFound(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase("hdr")) {
         inHeader = true;
      } else if (localName.equalsIgnoreCase("ftr")) {
         inFooter = true;
      } else if (localName.equalsIgnoreCase("Table")) {

      } else if (localName.equalsIgnoreCase("Worksheet")) {

      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler#endElementWithClearContents(java.lang.String,
    *      java.lang.String, java.lang.String)
    */
   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
      try {
         if (localName.equalsIgnoreCase("t")) {
            if (inHeader) {
               addContentsTo(headerText);
            } else if (inFooter) {
               addContentsTo(footerText);
            }
         } else if (localName.equalsIgnoreCase("hdr")) {
            inHeader = false;
         } else if (localName.equalsIgnoreCase("ftr")) {
            inFooter = false;
         }
      } catch (IOException ex) {
         throw new SAXException(ex);
      }
   }

   public String getHeaderText() {
      return headerText.toString();
   }

   public String getFooterText() {
      return footerText.toString();
   }
}