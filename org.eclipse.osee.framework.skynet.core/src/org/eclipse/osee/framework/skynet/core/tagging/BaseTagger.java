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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
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

      if (artifact == null) throw new IllegalArgumentException("A null artifact can not be tagged.");

      List<String> textString = new LinkedList<String>();

      for (DynamicAttributeManager attributeManager : artifact.getAttributes()) {
         for (Attribute attribute : attributeManager.getAttributes()) {
            String attrName = attributeManager.getDescriptor().getName();
            if (ignoreAttributeNames.contains(attrName)) continue;
            if (attrName.equals(WordAttribute.CONTENT_NAME)) {
               textString.add(WordUtil.textOnly(attribute.getStringData()));
            } else {
               textString.add(attribute.getStringData());
            }
         }
      }
      return textString;
   }
}
