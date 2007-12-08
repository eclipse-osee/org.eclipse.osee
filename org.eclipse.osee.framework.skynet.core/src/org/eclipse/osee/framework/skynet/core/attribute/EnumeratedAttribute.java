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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class EnumeratedAttribute extends Attribute {
   private String[] choices;
   // When an enumerated attribute is required for an artifact, yet doesn't exist yet, it is created upon
   // init of the artifact and given the "Unspecified" value
   public static String UNSPECIFIED_VALUE = "Unspecified";

   public EnumeratedAttribute(String name) {
      super(new VarcharMediaResolver(), name);
   }

   @Override
   public String getTypeName() {
      return "Enumerated";
   }

   @SuppressWarnings("unchecked")
   @Override
   public void setValidityXml(String validityXml) throws SAXException, ParserConfigurationException, IOException {
      Document document = Jaxp.readXmlDocument(validityXml);

      Element choicesElement = document.getDocumentElement();
      NodeList enumerations = choicesElement.getElementsByTagName("Enum");

      choices = new String[enumerations.getLength()];
      for (int i = 0; i < choices.length; i++) {
         choices[i] = enumerations.item(i).getTextContent();
      }

      setDirty();
   }

   public String[] getChoices() {
      return choices;
   }
}
