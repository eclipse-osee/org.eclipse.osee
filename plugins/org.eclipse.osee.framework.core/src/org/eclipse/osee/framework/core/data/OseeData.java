/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class OseeData {

   public static final String ART_ID_SEQ = "SKYNET_ART_ID_SEQ";
   public static final String ATTR_ID_SEQ = "SKYNET_ATTR_ID_SEQ";
   public static final String REL_LINK_ID_SEQ = "SKYNET_REL_LINK_ID_SEQ";
   public static final String GAMMA_ID_SEQ = "SKYNET_GAMMA_ID_SEQ";

   public static final String BRANCH_ID_SEQ = "SKYNET_BRANCH_ID_SEQ";
   public static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";
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
            OseeLog.log(OseeData.class, Level.SEVERE, ex);
         }
      }
      if (!project.isOpen()) {
         OseeLog.logf(OseeData.class, Level.INFO, "[%s] project is closed; re-opening", OSEE_DATA_FOLDER_NAME);
         try {
            project.open(null);
         } catch (CoreException e) {
            OseeLog.log(OseeData.class, Level.SEVERE, e);
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

   public static File getWorkspaceFile(String path) {
      IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      return new File(workspaceRoot.getFile(new Path(path)).getLocation().toString());
   }

   public static IFolder getFolder(String name) {
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
