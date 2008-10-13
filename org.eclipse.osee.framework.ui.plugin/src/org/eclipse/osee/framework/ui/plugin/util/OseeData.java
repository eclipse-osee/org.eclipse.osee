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

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

/**
 * This class provides a front end to writing files to a common osee.data directory in the workspace. This dir is
 * invisible to Eclipse Navigator and Package Explorer. It is provided as a common repository for files that need to be
 * created and retained by any plugin, but don't need to be visible to the user. This class does nothing more than
 * ensure the directory is created and provide a way to get the path for other plugins to use.
 * 
 * @author Donald G. Dunne
 */
public class OseeData {
   private static final IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
   private static String oseeDataPathName = ".osee.data";
   private static final IPath oseeDataPath = workspacePath.append(oseeDataPathName);
   private static final File oseeDir = oseeDataPath.toFile();
   private static IProject project;

   static {
      if (!oseeDir.exists()) {
         if (!oseeDir.mkdir()) {
            OseeLog.log(OseeUiActivator.class, Level.WARNING, "Can't create " + oseeDataPathName + " dir.");
         }
      }

      createProject();
   }

   public static IPath getPath() {
      return oseeDataPath;
   }

   public static File getFile(String filename) {
      return new File(oseeDir, filename);
   }

   public static IFile getIFile(String fileName) {
      return project.getFile(fileName);
   }

   public static IFile getIFile(String fileName, InputStream in) throws OseeCoreException {
      return getIFile(fileName, in, false);
   }

   public static IFile getIFile(String fileName, InputStream in, boolean overwrite) throws OseeCoreException {
      IFile iFile = project.getFile(fileName);
      if (!iFile.exists() || overwrite) {
         AIFile.writeToFile(iFile, in);
      }
      return iFile;
   }

   private static boolean createProject() {
      IWorkspaceRoot root = OseeUiActivator.getWorkspaceRoot();
      project = root.getProject(oseeDataPathName);
      if (!project.exists()) {
         try {
            project.create(null);
         } catch (CoreException ex) {
            ex.printStackTrace();
            return false;
         }
      }
      try {
         project.open(null);
      } catch (CoreException e) {
         e.printStackTrace();
         return false;
      }
      return true;
   }

   /**
    * @return Returns the project.
    */
   public static IProject getProject() {
      return project;
   }

   public static IFolder getFolder(String name) throws OseeCoreException {
      try {
         IFolder folder = project.getFolder(name);

         if (!folder.exists()) {
            folder.create(true, true, null);
         }
         return folder;
      } catch (CoreException ex) {
         throw new OseeCoreException(ex);
      }
   }
}