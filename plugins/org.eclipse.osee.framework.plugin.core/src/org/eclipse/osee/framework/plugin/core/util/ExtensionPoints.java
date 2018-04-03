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

package org.eclipse.osee.framework.plugin.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.internal.Activator;
import org.osgi.framework.Bundle;

/**
 * Use ExtensionDefinedObjects if possible
 * 
 * @author Ryan D. Brooks
 */
public class ExtensionPoints {
   public static List<IConfigurationElement> getExtensionElements(Plugin plugin, String extensionPointName, String elementName) {
      Bundle bundle = plugin.getBundle();
      return getExtensionElements(bundle.getSymbolicName() + "." + extensionPointName, elementName);
   }

   public static List<IConfigurationElement> getExtensionElements(String extensionPointId, String elementName) {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry == null) {
         throw new IllegalStateException("The extension registry is unavailable");
      }

      IExtensionPoint point = extensionRegistry.getExtensionPoint(extensionPointId);
      if (point == null) {
         throw new IllegalArgumentException("The extension point " + extensionPointId + " does not exist");
      }

      IExtension[] extensions = point.getExtensions();
      ArrayList<IConfigurationElement> elementsList = new ArrayList<>(extensions.length * 3);

      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         for (IConfigurationElement element : elements) {
            if (element.getName().equalsIgnoreCase(elementName)) {
               elementsList.add(element);
            }
         }
      }
      return elementsList;
   }

   /**
    * Return extension point unique ids if type extensionPointId
    * 
    * @param extensionPointId <plugin>.Point Id
    * @param extensionPointUniqueIds array of unique ids
    */
   public static List<IExtension> getExtensionsByUniqueId(String extensionPointId, Collection<String> extensionPointUniqueIds) {
      List<IExtension> extensions = new ArrayList<>();
      for (String entensionPointUniqueId : extensionPointUniqueIds) {
         IExtension extension = Platform.getExtensionRegistry().getExtension(entensionPointUniqueId);
         if (extension == null) {
            OseeLog.log(Activator.class, Level.SEVERE,
               "Unable to locate extension [" + entensionPointUniqueId + "]");
         } else {
            String thisPointId = extension.getExtensionPointUniqueIdentifier();
            if (extensionPointId.equals(thisPointId)) {
               extensions.add(extension);
            } else {
               OseeLog.log(Activator.class, Level.SEVERE,
                  "Unknown extension id [" + thisPointId + "] from extension [" + entensionPointUniqueId + "]");
            }
         }
      }
      return extensions;
   }

   public static List<String> getExtensionsPointUniqueIds(String extensionPointId) {
      List<String> extensionPointIds = new ArrayList<>();
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(extensionPointId);
      if (point == null) {
         throw new IllegalArgumentException("The extension point " + extensionPointId + " does not exist");
      }

      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         extensionPointIds.add(extension.getUniqueIdentifier());
      }
      return extensionPointIds;
   }

   public static IConfigurationElement getExtensionElement(String extensionPointId, String elementName) {
      List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(extensionPointId, elementName);

      if (elements.isEmpty()) {
         throw new IllegalArgumentException(
            "no elements named " + elementName + " for " + extensionPointId + " where found.");
      }
      if (elements.size() > 1) {
         throw new IllegalArgumentException(
            elements.size() + " elements named " + elementName + " for " + extensionPointId + " where found.  Expected exactly one.");
      }
      return elements.get(0);
   }

}