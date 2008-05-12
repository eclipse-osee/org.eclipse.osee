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

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Ryan D. Brooks
 */
public class EnumeratedAttribute extends CharacterBackedAttribute<String> {
   private String[] choices;
   // When an enumerated attribute is required for an artifact, yet doesn't exist yet, it is created upon
   // init of the artifact and given the "Unspecified" value
   public static String UNSPECIFIED_VALUE = "Unspecified";

   public EnumeratedAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);

      try {
         Document document = Jaxp.readXmlDocument(attributeType.getValidityXml());
         Element choicesElement = document.getDocumentElement();
         NodeList enumerations = choicesElement.getElementsByTagName("Enum");

         choices = new String[enumerations.getLength()];
         for (int i = 0; i < choices.length; i++) {
            choices[i] = enumerations.item(i).getTextContent();
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         choices = new String[] {ex.getLocalizedMessage()};
      }
   }

   public String[] getChoices() {
      return choices;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public String getValue() {
      return getAttributeDataProvider().getValueAsString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void setValue(String value) {
      getAttributeDataProvider().setValue(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      String toDisplay = getAttributeDataProvider().getDisplayableString();
      return Strings.isValid(toDisplay) ? toDisplay : "<Select>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setFromString(java.lang.String)
    */
   @Override
   public void setFromString(String value) throws Exception {
      String toSet = value;
      if (value != null) {
         for (String choice : choices) {
            if (value.equals(choice)) {
               toSet = choice;
               break;
            }
         }
      }
      setValue(toSet);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#initializeDefaultValue()
    */
   @Override
   public void initializeDefaultValue() {
      getAttributeDataProvider().setValue(getAttributeType().getDefaultValue());
   }
}