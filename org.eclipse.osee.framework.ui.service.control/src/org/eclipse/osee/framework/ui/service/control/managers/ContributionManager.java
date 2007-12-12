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
package org.eclipse.osee.framework.ui.service.control.managers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class ContributionManager extends ClassLoader {

   private static ContributionManager instance = null;
   private Map<String, String> extensionRegistryMap;
   private Map<String, Class<?>> classesloaded; // <String, Class>
   private List<String> bundleList; // <String>
   private Map<String, String> interfaceToRendererMap;
   private Map<String, String> interfaceToIconMap;
   private Logger logger = ConfigUtil.getConfigFactory().getLogger(ContributionManager.class);

   private ContributionManager() {
      super();
      extensionRegistryMap = new HashMap<String, String>();
      interfaceToIconMap = new HashMap<String, String>();
      loadFactoryBundleMap();
   }

   public static ContributionManager getInstance() {
      if (instance == null) {
         instance = new ContributionManager();
      }
      return instance;
   }

   // @SuppressWarnings("unchecked")
   private void loadFactoryBundleMap() {
      interfaceToRendererMap = new HashMap<String, String>();
      classesloaded = new HashMap<String, Class<?>>();
      bundleList = new ArrayList<String>();

      String registrationStatus = ContributionManager.class.getName() + " Registration: [ \n";
      if (extensionRegistryMap.size() != 0) return;
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry != null) {
         IExtensionPoint point =
               extensionRegistry.getExtensionPoint("org.eclipse.osee.framework.ui.service.control.ServiceView");
         if (point != null) {
            IExtension[] extensions = point.getExtensions();
            for (IExtension extension : extensions) {
               IConfigurationElement[] elements = extension.getConfigurationElements();
               String classname = null;
               String renderer = null;
               String imagePath = null;
               String bundleName = null;
               for (IConfigurationElement el : elements) {
                  if (el.getName().equals("ServiceHandler")) {
                     bundleName = el.getContributor().getName();
                     classname = el.getAttribute("ServiceInterface");

                     renderer = el.getAttribute("ServiceRenderer");
                     imagePath = el.getAttribute("ServiceIcon");

                     if (classname != null && bundleName != null) {
                        bundleList.add(el.getContributor().getName());
                        extensionRegistryMap.put(classname, bundleName);

                        registrationStatus += "[" + bundleName + "] [" + classname + "] ";

                        if (renderer != null) {
                           extensionRegistryMap.put(renderer, bundleName);
                           interfaceToRendererMap.put(classname, renderer);
                           registrationStatus += "[" + renderer + "]";
                        }

                        if (imagePath != null) {
                           extensionRegistryMap.put(imagePath, bundleName);
                           interfaceToIconMap.put(classname, imagePath);

                           registrationStatus += " [" + imagePath + "]\n";
                        } else {
                           registrationStatus += "\n";
                        }
                     }
                  }
               }
            }
         }
      }
      logger.log(Level.INFO, registrationStatus + "]\n");
   }

   public Map<String, String> getInterfaceToRendererMap() {
      return interfaceToRendererMap;
   }

   public Map<String, String> getInterfaceToIconMap() {
      return interfaceToIconMap;
   }

   protected synchronized Class<?> findClass(String classname) throws ClassNotFoundException {
      Class<?> loadedclass = classesloaded.get(classname);
      if (loadedclass != null) {
         return loadedclass;
      }

      if (extensionRegistryMap.containsKey(classname)) {
         String bundleName = (String) extensionRegistryMap.get(classname);
         Bundle bundle = Platform.getBundle(bundleName);
         try {
            Class<?> foundclass = bundle.loadClass(classname);
            if (foundclass != null) {
               classesloaded.put(classname, foundclass);
               return foundclass;
            }
         } catch (Exception ex) {
         }
      }

      for (int i = 0; i < bundleList.size(); i++) {
         try {
            Class<?> foundclass = Platform.getBundle((String) bundleList.get(i)).loadClass(classname);
            if (foundclass != null) {
               String bundleName = bundleList.remove(i);
               bundleList.add(0, bundleName);
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

   public ImageDescriptor getImageDescriptor(String imageFilePath) {
      if (imageFilePath != null && extensionRegistryMap.containsKey(imageFilePath)) {
         String bundleName = (String) extensionRegistryMap.get(imageFilePath);
         Bundle bundle = Platform.getBundle(bundleName);
         if (bundle != null) {

            // look for the image (this will check both the plug-in and fragment folders
            URL fullPathString = FileLocator.find(bundle, new Path(imageFilePath), null);
            if (fullPathString == null) {
               try {
                  fullPathString = new URL(imageFilePath);
               } catch (MalformedURLException e) {
                  return null;
               }
            }

            if (fullPathString != null) {
               return ImageDescriptor.createFromURL(fullPathString);
            }
         }
      }
      return null;
   }
}
