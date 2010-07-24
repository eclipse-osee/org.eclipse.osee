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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.io.IOException;
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.ote.core.ReturnStatus;
import org.eclipse.osee.ote.core.environment.BundleConfigurationReport;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.osgi.framework.Version;

public interface IRuntimeLibraryManager extends Xmlizable {
   /**
    * @param version
    * @deprecated use isBundleAvailable
    */
   @Deprecated
   boolean isMessageJarAvailable(String version);

   boolean isBundleAvailable(String symbolicName, String version, byte[] md5Digest);

   ReturnStatus isRunningJarVersions(String[] versions);

   void addRuntimeLibraryListener(RuntimeLibraryListener listener);

   void removeRuntimeLibraryListener(RuntimeLibraryListener listener);

   /**
    * @param jarData
    * @throws IOException
    * @deprecated see loadBundle
    */
   @Deprecated
   void addJarToClassLoader(byte[] jarData) throws IOException;

   void loadBundles(Collection<BundleDescription> bundles) throws Exception;

   BundleConfigurationReport checkBundleConfiguration(Collection<BundleDescription> bundles) throws Exception;

   void updateBundles(Collection<BundleDescription> bundles) throws Exception;

   void resetScriptLoader(String[] strings) throws Exception;

   Class<?> loadFromScriptClassLoader(String path) throws ClassNotFoundException;

   Class<?> loadClass(String name, Version version) throws ClassNotFoundException;

   Class<?> loadFromRuntimeLibraryLoader(String path) throws ClassNotFoundException;

   void cleanup();

}
