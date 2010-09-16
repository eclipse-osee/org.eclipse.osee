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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;

/**
 * @author Ryan D. Brooks
 */
public final class AIFile {

   private AIFile() {
      // utility class
   }

   public static void writeToFile(IFile file, InputStream in) throws OseeCoreException {
      try {
         if (file.exists()) {
            file.setCharset("UTF-8", new NullProgressMonitor());
            file.setContents(in, true, false, null); // steam will be closed before return
         } else {
            file.create(in, true, null);
            in.close();
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public static void writeToFile(IFile file, String string) throws OseeCoreException {
      writeToFile(file, string, "UTF-8");
   }

   public static void writeToFile(IFile file, String string, String charcode) throws OseeCoreException {
      try {
         writeToFile(file, Streams.convertStringToInputStream(string, charcode));
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public static IFile constructIFile(File file) throws OseeArgumentException {
      IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      IFile[] files = workspaceRoot.findFilesForLocationURI(file.toURI());

      if (files.length == 1) {
         return files[0];
      } else {
         throw new OseeArgumentException(
            String.format("expected findFilesForLocationURI for %s to find 1 file but %d found",
               file.getAbsolutePath(), files.length));
      }
   }

   public static IFile constructIFile(String fullPath) throws OseeArgumentException {
      return constructIFile(new File(fullPath));
   }
}