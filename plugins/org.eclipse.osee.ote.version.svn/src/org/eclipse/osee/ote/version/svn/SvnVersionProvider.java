/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.version.svn;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformationProvider;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.connector.SVNDepth;
import org.eclipse.team.svn.core.connector.SVNEntryInfo;
import org.eclipse.team.svn.core.connector.SVNEntryRevisionReference;
import org.eclipse.team.svn.core.extension.CoreExtensionsManager;
import org.eclipse.team.svn.core.operation.SVNNullProgressMonitor;
import org.eclipse.team.svn.core.utility.SVNUtility;

public class SvnVersionProvider implements FileVersionInformationProvider {

   protected boolean isSvn(File file) {
      File svn = new File(file, SVNUtility.getSVNFolderName());
      return svn.exists();
   }

   @Override
   public void getFileVersions(List<File> scriptFiles, Map<File, FileVersion> versions) {

      IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      ScriptToProject collection = new ScriptToProject(workspaceProjects);

      for (File scriptFile : scriptFiles) {
         collection.add(scriptFile);
      }

      ISVNConnector proxy = CoreExtensionsManager.instance().getSVNConnectorFactory().createConnector();
      for (String projectName : collection.getProjectsSet()) {
         try {
            SVNEntryInfo[] st =
               SVNUtility.info(proxy, new SVNEntryRevisionReference(projectName), SVNDepth.INFINITY,
                  new SVNNullProgressMonitor());
            for (SVNEntryInfo entry : st) {
               String svnEntryPath = entry.path;
               String itemToMatch = svnEntryPath.substring(svnEntryPath.lastIndexOf("/") + 1);
               File scriptFile = collection.getScriptFileMatch(projectName, itemToMatch);
               if (scriptFile != null) {
                  versions.put(scriptFile, new SvnFileVersion(entry));
               }
            }
         } catch (Exception ex) {
            OseeLog.logf(getClass(), Level.SEVERE, "SVNConnectorException while retrieving script SVN info ", ex);
         }
      }
   }
}
