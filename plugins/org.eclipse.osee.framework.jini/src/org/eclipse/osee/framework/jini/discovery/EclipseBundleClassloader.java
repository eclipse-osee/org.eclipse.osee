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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jini.JiniPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * This class assumes that code is not downloaded from services but already resides in the eclipse install as a plugin.
 * A result of this is that versions of the service and the plugins being used need to match.
 * 
 * @author Andrew M. Finkbeiner
 */
public class EclipseBundleClassloader extends ClassLoader {
   private Map<String, Class<?>> classesloaded;
   private Map<String, Bundle> bundleLoaded;

   public EclipseBundleClassloader(List<String> bundleNames) {
      this(bundleNames, EclipseBundleClassloader.class.getClassLoader());
   }

   public EclipseBundleClassloader(List<String> bundleNames, ClassLoader parent) {
      super(parent);
      classesloaded = new HashMap<String, Class<?>>();
      bundleLoaded = new HashMap<String, Bundle>();
      for (int i = 0; i < bundleNames.size(); i++) {
         String name = bundleNames.get(i);
         Bundle bundle = Platform.getBundle(name);
         if (bundle == null) {
            OseeLog.log(JiniPlugin.class, Level.WARNING, "The bundle " + name + " does not exist");
         } else {
            bundleLoaded.put(bundle.getSymbolicName(), bundle);
         }
      }
   }

   public EclipseBundleClassloader(Bundle bundle) {
      super(EclipseBundleClassloader.class.getClassLoader());
      classesloaded = new HashMap<String, Class<?>>();
      bundleLoaded = new HashMap<String, Bundle>();
      bundleLoaded.put(bundle.getSymbolicName(), bundle);
   }

   protected synchronized Class<?> findClass(String classname) throws ClassNotFoundException {
      Class<?> loadedclass = classesloaded.get(classname);
      if (loadedclass != null) {
         return loadedclass;
      }
      Iterator<String> it = bundleLoaded.keySet().iterator();
      while (it.hasNext()) {
         String key = it.next();
         Bundle bundle = bundleLoaded.get(key);
         try {
            Class<?> foundclass = bundle.loadClass(classname);
            if (!classesloaded.containsKey(classname)) {
               classesloaded.put(classname, foundclass);
            }
            return foundclass;
         } catch (NoClassDefFoundError err) {
            OseeLog.log(JiniPlugin.class, Level.SEVERE, "Caught Error: bundle = " + bundle.getSymbolicName(), err);
         } catch (ClassNotFoundException ex) {
            // Do nothing
         }
      }
      return this.getParent().loadClass(classname);
   }

   @SuppressWarnings("unchecked")
   public Class loadClass(String classname) throws ClassNotFoundException {
      return loadClass(classname, false);
   }

}
