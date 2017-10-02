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
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.internal.PluginCoreActivator;

/**
 * This class provides a front end to writing files to a common osee.data directory in the workspace. This dir is
 * invisible to Eclipse Navigator and Package Explorer. It is provided as a common repository for files that need to be
 * created and retained by any plugin, but don't need to be visible to the user. This class does nothing more than
 * ensure the directory is created and provide a way to get the path for other plugins to use.
 *
 * @author Donald G. Dunne
 */
public final class OseeData {
   private static final String OSEE_DATA_FOLDER_NAME = "osee.data";
   private static IProject project;

   private OseeData() {
      // Utility class
   }

   public static synchronized void ensureProjectReady() {
      if (project == null) {
         IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
         project = workspaceRoot.getProject(OSEE_DATA_FOLDER_NAME);
      }
      if (!project.exists()) {
         try {
            project.create(null);
            project.open(null);
         } catch (CoreException ex) {
            OseeLog.log(PluginCoreActivator.class, Level.SEVERE, ex);
         }
      }
      if (!project.isOpen()) {
         OseeLog.logf(PluginCoreActivator.class, Level.INFO, "[%s] project is closed; re-opening",
            OSEE_DATA_FOLDER_NAME);
         try {
            project.open(null);
         } catch (CoreException e) {
            OseeLog.log(PluginCoreActivator.class, Level.SEVERE, e);
         }
      }
   }

   public static IProject getProject() {
      ensureProjectReady();
      return project;
   }

   public static IPath getPath() {
      IPath workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
      return workspacePath.append(OSEE_DATA_FOLDER_NAME);
   }

   public static File getFile(String filename) {
      ensureProjectReady();
      return new File(getPath().toFile(), filename);
   }

   public static IFile getIFile(String fileName) {
      return getProject().getFile(fileName);
   }

   public static IFile getIFile(String fileName, InputStream in)  {
      return getIFile(fileName, in, false);
   }

   public static IFile getIFile(String fileName, InputStream in, boolean overwrite)  {
      IFile iFile = getProject().getFile(fileName);
      if (!iFile.exists() || overwrite) {
         AIFile.writeToFile(iFile, in);
      }
      return iFile;
   }

   public static File getWorkspaceFile(String path) {
      IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      return new File(workspaceRoot.getFile(new Path(path)).getLocation().toString());
   }

   public static IFolder getFolder(String name)  {
      try {
         IFolder folder = getProject().getFolder(name);
         if (!folder.exists()) {
            folder.create(true, true, null);
         }
         return folder;
      } catch (CoreException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}