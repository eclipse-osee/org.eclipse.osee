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
package org.eclipse.osee.framework.skynet.core.tagging;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class BaseTagger extends Tagger {

   private static final List<String> ignoreAttributeNames = customizeTaggerFromExtensionPoints();

   private static List<String> customizeTaggerFromExtensionPoints() {
      List<String> ignoreAttributes = new ArrayList<String>();
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(
                  "org.eclipse.osee.framework.skynet.core.TaggerCustomization");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement element : elements) {
            if (element.getName().equals("ignoreSkynetAttribute")) {
               ignoreAttributes.add(element.getAttribute("attributeName"));
            }
         }
      }
      return ignoreAttributes;
   }

   @Override
   public Collection<String> getTextStrings(Artifact artifact) throws SQLException {
      List<String> textString = new LinkedList<String>();

      for (Attribute<?> attribute : artifact.getAttributes()) {
         String attributeTypeName = attribute.getAttributeType().getName();
         attributeTypeName = AttributeTypeManager.getTypeWithWordContentCheck(artifact, attributeTypeName).getName();

         if (!ignoreAttributeNames.contains(attributeTypeName)) {
            if (attributeTypeName.equals(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
                  WordAttribute.CONTENT_NAME).getName())) {
               textString.add(WordUtil.textOnly(attribute.toString()));
            } else {
               textString.add(attribute.toString());
            }
         }
      }
      return textString;
   }

   private static final char[] tagChars =
         new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
               'l', 'm', 'n', 'o', 'p', 'r', 's', 't', 'u', 'v', 'w', 'y'};

   /**
    * create a bit-packed tag that will fit in a 64-bit integer that can provide an extremely quick search mechanism for
    * for the first pass. The second pass will do a full text search to provide more exact matches. The tag will
    * represent up to 12 characters (all that can be stuffed into 64-bits). Longer search tags will be turned into
    * consecutive search tags
    * 
    * @param insertParameters
    * @param attribute
    * @param textValue
    */
   private void fastTag(Collection<Object[]> insertParameters, Attribute<?> attribute, String textValue) {
      int tagBitsPos = 0;
      long tagBits = 0;
      for (int index = 0; index < textValue.length(); index++) {
         char c = textValue.charAt(index);

         if (c == '\t' || c == '\n' || c == '\r' || tagBitsPos == 60) {
            if (tagBitsPos > 10) {
               insertParameters.add(new Object[] {SQL3DataType.BIGINT, tagBits});
            }
            tagBits = 0;
            tagBitsPos = 0;
         } else {
            if (c >= 'A' && c <= 'Z') {
               c += 32;
            }
            int pos = Arrays.binarySearch(tagChars, c);
            if (pos < 0) {
               tagBits |= 0x3F << tagBitsPos;
            } else {
               tagBits |= pos << tagBitsPos;
            }
            tagBitsPos += 4;
         }
      }
   }
}
