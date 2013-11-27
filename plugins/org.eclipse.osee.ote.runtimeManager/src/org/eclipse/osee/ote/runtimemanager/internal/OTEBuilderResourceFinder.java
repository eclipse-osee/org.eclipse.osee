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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.server.ResourceFinder;
import org.eclipse.osee.ote.ui.builder.OTEPackagingBuilder;

/**
 * Resource finder for the OTE builder jars.  This will enable the downloading of jars from the 
 * same http server that provides the precompiled jars.
 * 
 * @author Andrew M. Finkbeiner
 */
public class OTEBuilderResourceFinder extends ResourceFinder {
   private File rootFolder;

   public OTEBuilderResourceFinder() {
      super();
      rootFolder = OTEPackagingBuilder.getWorkspaceArchiveFolder();
   }

   @Override
   public byte[] find(String path) throws IOException {
      try {
         File bundleFile = new File(rootFolder, path);
         if(bundleFile.exists()){
            return Lib.inputStreamToBytes(new FileInputStream(bundleFile));
         }
      } catch (Exception ex) {
         OseeLog.logf(getClass(), Level.SEVERE, "Error trying to read: [%s]", path);
      }
      return null;
   }

   @Override
   public void dispose() {
   }
}
