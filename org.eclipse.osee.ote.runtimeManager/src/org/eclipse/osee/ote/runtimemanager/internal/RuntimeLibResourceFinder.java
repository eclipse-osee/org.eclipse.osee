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
package org.eclipse.osee.ote.runtimemanager.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.server.ResourceFinder;
import org.eclipse.osee.ote.runtimemanager.BundleInfo;
import org.eclipse.osee.ote.runtimemanager.SafeWorkspaceTracker;

/** 
 * @author Robert A. Fisher
 *
 */
public class RuntimeLibResourceFinder extends ResourceFinder {
   private SafeWorkspaceTracker safeWorkspaceTracker;

   /**
    * @param safeWorkspaceTracker 
    * @param runtimeManager
    */
   public RuntimeLibResourceFinder(SafeWorkspaceTracker safeWorkspaceTracker) {
      super();
      this.safeWorkspaceTracker = safeWorkspaceTracker;
   }

   @Override
   public byte[] find(String path) throws IOException {
      try {
         Collection<BundleInfo> runtimeLibs = safeWorkspaceTracker.getRuntimeLibs();
         for (BundleInfo info : runtimeLibs) {
            if (info.getSymbolicName().equals(path)) {
               return Lib.inputStreamToBytes(new FileInputStream(info.getFile()));	
            }
         }
      } catch (CoreException ex) {
         // TODO
         ex.printStackTrace();
      }
      return null;
   }
   
   @Override
   public void dispose() {
   }
}
