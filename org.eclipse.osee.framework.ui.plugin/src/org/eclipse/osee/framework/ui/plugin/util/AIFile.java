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
package org.eclipse.osee.framework.ui.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;

/**
 * @author Ryan D. Brooks
 */
public class AIFile {

   public static void writeToFile(IFile file, InputStream in) throws CoreException, IOException {
      if (file.exists()) {
         file.setCharset("UTF-8", new NullProgressMonitor());
         file.setContents(in, true, false, null); // steam will be closed before return
      } else {
         file.create(in, true, null);
         in.close();
      }
   }

   public static void writeToFile(IFile file, String string) throws CoreException, IOException {
      writeToFile(file, Streams.convertStringToInputStream(string, "UTF-8"));
   }

   public static IFile constructIFile(String fullPath) {
      IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      IFile[] files = workspaceRoot.findFilesForLocation(Path.fromOSString(fullPath));
      if (files == null || files.length == 0) return null;
      return workspaceRoot.findFilesForLocation(Path.fromOSString(fullPath))[0];
   }
}