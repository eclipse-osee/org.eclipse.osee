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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Ryan D. Brooks
 */
public class EnumeratedAttribute extends StringAttribute {
   // When an enumerated attribute is required for an artifact, yet doesn't exist yet, it is created upon
   // init of the artifact and given the "Unspecified" value
   public static String UNSPECIFIED_VALUE = "Unspecified";

   public EnumeratedAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);

   }

   public static List<String> getChoices(AttributeType attributeType) {
      List<String> choices = new ArrayList<String>();
      try {
         Document document = Jaxp.readXmlDocument(attributeType.getValidityXml());
         Element choicesElement = document.getDocumentElement();
         NodeList enumerations = choicesElement.getElementsByTagName("Enum");

         for (int i = 0; i < enumerations.getLength(); i++) {
            choices.add(enumerations.item(i).getTextContent());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return choices;
   }

   public String[] getChoices() {
      List<String> choices = EnumeratedAttribute.getChoices(getAttributeType());
      return choices.toArray(new String[choices.size()]);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() throws OseeDataStoreException {
      String toDisplay = getAttributeDataProvider().getDisplayableString();
      return Strings.isValid(toDisplay) ? toDisplay : "<Select>";
   }
}