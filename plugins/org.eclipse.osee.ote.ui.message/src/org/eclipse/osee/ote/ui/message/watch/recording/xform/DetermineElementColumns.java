/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch.recording.xform;

import java.util.LinkedHashSet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrew M. Finkbeiner
 */
public class DetermineElementColumns extends AbstractSaxHandler {

   private final String[] pubSubHeaderElementsToStore = new String[] {"timeTag", "sequenceNum"};
   private final LinkedHashSet<String> elementNames = new LinkedHashSet<String>();
   private String message;

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {

      if ("Update".equals(localName)) {
         message = attributes.getValue("message");
      } else if ("PubSubHeaderInfo".equals(localName)) {
         for (String str : pubSubHeaderElementsToStore) {
            elementNames.add(String.format("%s.PubSubHeader.%s", message, str));
         }
      } else if ("Element".equals(localName)) {
         elementNames.add(String.format("%s.%s", message, attributes.getValue("name")));
      }
   }

   /**
    * @return String[]
    */
   public String[] getElementColumns() {
      return elementNames.toArray(new String[elementNames.size()]);
   }

}
