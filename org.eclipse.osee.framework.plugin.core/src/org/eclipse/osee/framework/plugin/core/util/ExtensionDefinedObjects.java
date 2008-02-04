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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class ExtensionDefinedObjects<T> {

   private List<T> loadedObjects;
   private Map<String, T> objectsByID;

   private String extensionPointId;
   private String elementName;
   private String classNameAttribute;

   public ExtensionDefinedObjects(String extensionPointId, String elementName, String classNameAttribute) {
      if (true != Strings.isValid(extensionPointId)) {
         throw new IllegalStateException("The extension point id is not valid");
      }
      if (true != Strings.isValid(elementName)) {
         throw new IllegalStateException("The element name is not valid");
      }
      if (true != Strings.isValid(classNameAttribute)) {
         throw new IllegalStateException("The class name attribute is not valid");
      }

      this.extensionPointId = extensionPointId;
      this.elementName = elementName;
      this.classNameAttribute = classNameAttribute;
   }

   public List<T> getObjects() {
      checkInitialized();
      return loadedObjects;
   }

   public T getObjectById(String id) {
      checkInitialized();
      return objectsByID.get(id);
   }

   private void checkInitialized() {
      if (!isInitialized()) {
         initialize(extensionPointId, elementName, classNameAttribute);
      }
   }

   private boolean isInitialized() {
      return loadedObjects != null && objectsByID != null;
   }

   @SuppressWarnings("unchecked")
   private void initialize(String extensionPointId, String elementName, String classNameAttribute) {
      loadedObjects = new ArrayList<T>();
      objectsByID = new HashMap<String, T>();
      List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(extensionPointId, elementName);
      for (IConfigurationElement element : elements) {
         IExtension extension = ((IExtension) element.getParent());
         String identifier = extension.getUniqueIdentifier();
         String className = element.getAttribute(classNameAttribute);
         String bundleName = element.getContributor().getName();

         if (Strings.isValid(bundleName) && Strings.isValid(className)) {
            try {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> taskClass = bundle.loadClass(className);
               T object = null;
               try {
                  Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
                  object = (T) getInstance.invoke(null, new Object[] {});
               } catch (Exception ex) {
                  object = (T) taskClass.newInstance();
               }
               if (object != null) {
                  loadedObjects.add(object);
                  objectsByID.put(identifier, object);
               }
            } catch (Exception ex) {
               throw new IllegalArgumentException(String.format("Unable to Load: [%s - %s]", bundleName, className), ex);
            }
         }
      }
      if (loadedObjects.size() == 0) {
         throw new IllegalArgumentException(String.format(
               "No Objects loaded for [%s] with element name [%s] and attribute [%s]", extensionPointId, elementName,
               classNameAttribute));
      }
   }

   public String toString() {
      return getObjects().toString();
   }
}
