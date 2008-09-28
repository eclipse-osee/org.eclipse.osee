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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.providers.AbstractAttributeDataProvider;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class AttributeExtensionManager {
   private static final String CLASS_ID = "class";
   private static final String ATTRIBUTE_TYPE = SkynetActivator.PLUGIN_ID + ".AttributeType";
   private static final String ATTRIBUTE_DATA_PROVIDER_TYPE = SkynetActivator.PLUGIN_ID + ".AttributeDataProvider";

   private static final String[] attributeBaseTypes =
         new String[] {"CharacterBackedAttributeType", "BinaryBackedAttributeType"};
   private static final String[] attributeProviderBaseTypes =
         new String[] {"CharacterAttributeDataProvider", "BinaryAttributeDataProvider"};

   private static final AttributeExtensionManager instance = new AttributeExtensionManager();

   private Map<String, Pair<String, String>> attributeTypeClasses;
   private Map<String, Pair<String, String>> attributeDataProviderClasses;

   private AttributeExtensionManager() {
      this.attributeTypeClasses = null;
      this.attributeDataProviderClasses = null;
   }

   public static Class<? extends Attribute<?>> getAttributeClassFor(String name) throws ClassNotFoundException {
      if (instance.attributeTypeClasses == null) {
         instance.attributeTypeClasses = instance.loadExtensions(ATTRIBUTE_TYPE, attributeBaseTypes, CLASS_ID);
      }
      Pair<String, String> entry = instance.attributeTypeClasses.get(name);
      if (entry == null) {
         throw new ClassNotFoundException(String.format("Unable to find class for: [%s]", name));
      }

      return instance.loadClass(entry.getKey(), entry.getValue());
   }

   public static Class<? extends AbstractAttributeDataProvider> getAttributeProviderClassFor(String name) throws ClassNotFoundException {
      if (instance.attributeDataProviderClasses == null) {
         instance.attributeDataProviderClasses =
               instance.loadExtensions(ATTRIBUTE_DATA_PROVIDER_TYPE, attributeProviderBaseTypes, CLASS_ID);
      }
      Pair<String, String> entry = instance.attributeDataProviderClasses.get(name);
      if (entry == null) {
         throw new ClassNotFoundException(String.format("Unable to find class for: [%s]", name));
      }
      return instance.loadClass(entry.getKey(), entry.getValue());
   }

   @SuppressWarnings("unchecked")
   private <T> Class<T> loadClass(String bundleName, String className) throws ClassNotFoundException {
      Class<T> toReturn = null;
      try {
         Bundle bundle = Platform.getBundle(bundleName);
         toReturn = bundle.loadClass(className);
      } catch (ClassNotFoundException ex) {
         throw new ClassNotFoundException(String.format("Unable to Load: [%s - %s]", bundleName, className), ex);
      }
      return toReturn;
   }

   private Map<String, Pair<String, String>> loadExtensions(String extensionPointId, String[] elementNames, String classNameAttribute) {
      Map<String, Pair<String, String>> toReturn = new HashMap<String, Pair<String, String>>();
      for (String elementName : elementNames) {
         List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(extensionPointId, elementName);
         for (IConfigurationElement element : elements) {
            IExtension extension = ((IExtension) element.getParent());
            String name = extension.getUniqueIdentifier();
            String className = element.getAttribute(classNameAttribute);
            String bundleName = element.getContributor().getName();

            if (Strings.isValid(bundleName) && Strings.isValid(className)) {
               toReturn.put(name, new Pair<String, String>(bundleName, className));
            }
         }
      }
      if (toReturn.size() == 0) {
         throw new IllegalStateException(String.format(
               "No Objects loaded for [%s] with element names [%s] and attribute [%s]", extensionPointId, elementNames,
               classNameAttribute));
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return super.toString();
   }
}
