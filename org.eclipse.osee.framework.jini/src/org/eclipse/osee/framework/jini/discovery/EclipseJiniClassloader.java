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
package org.eclipse.osee.framework.jini.discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * This class assumes that code is not downloaded from services but already resides in the eclipse install as a plugin.
 * A result of this is that versions of the service and the plugins being used need to match.
 * 
 * @author Andrew M. Finkbeiner
 */
public class EclipseJiniClassloader extends ClassLoader {
   private static EclipseJiniClassloader singleton;
   private Map<String, Class<?>> classesloaded;
   private Map<String, String> registrationMap;
   private List<String> bundleList;

   public static EclipseJiniClassloader getInstance() {
      if (singleton == null) {
         singleton = new EclipseJiniClassloader();
      }
      return singleton;
   }

   public EclipseJiniClassloader() {
      classesloaded = new HashMap<String, Class<?>>();
      bundleList = new ArrayList<String>();
      bundleList.add("net.jini");

      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.jini.JiniInterface");
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         registrationMap = new HashMap<String, String>();
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("Interface")) {
               String classname = el.getAttribute("classname");
               String bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  registrationMap.put(classname, bundleName);
               }
            } else if (el.getName().equals("RegisterBundle")) {
               bundleList.add(el.getContributor().getName());
            }
         }
      }
   }

   @Override
   protected synchronized Class<?> findClass(String classname) throws ClassNotFoundException {
      Class<?> loadedclass = classesloaded.get(classname);
      if (loadedclass != null) {
         return loadedclass;
      }

      if (registrationMap.containsKey(classname)) {
         String bundleName = (String) registrationMap.get(classname);
         Bundle bundle = Platform.getBundle(bundleName);
         try {
            Class<?> foundclass = bundle.loadClass(classname);
            if (foundclass != null) {
               System.out.println(foundclass.getName());
               classesloaded.put(classname, foundclass);
               return foundclass;
            }
         } catch (Exception ex) {
         }
      }

      for (int i = 0; i < bundleList.size(); i++) {
         try {
            Class<?> foundclass = Platform.getBundle(bundleList.get(i)).loadClass(classname);
            if (foundclass != null) {
               String bundle = bundleList.remove(i);
               bundleList.add(0, bundle);
            }
            if (!classesloaded.containsKey(classname)) {
               classesloaded.put(classname, foundclass);
            }
            return foundclass;

         } catch (Exception ex) {
            // Do nothing
         }
      }
      return super.findClass(classname);
   }
}
