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
package org.eclipse.osee.ote.define.operations;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.define.OteDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractRemoteResourceRequestTemplate {
   private static final int CONNECTION_TIMEOUT = 120000;
   private static final int CONNECTION_READ_TIMEOUT = 10000;
   private IFile remoteFile;
   private final String tempFolderName;
   private final String remoteRequestUrl;

   public AbstractRemoteResourceRequestTemplate(String tempFolderName, String remoteRequestUrl) {
      super();
      this.tempFolderName = tempFolderName;
      this.remoteRequestUrl = remoteRequestUrl;
      this.remoteFile = null;
      createTemporaryDirectory();
   }

   private void createTemporaryDirectory() {
      try {
         final IProject project = OseeData.getProject();
         final IFolder folder = project.getFolder(tempFolderName + File.separator);
         if (folder != null && folder.exists() != true) {
            ContainerCreator containerCreator = new ContainerCreator(folder.getWorkspace(), folder.getFullPath());
            containerCreator.createContainer(new NullProgressMonitor());
         }
      } catch (CoreException ex1) {
         OseeLog.log(OteDefinePlugin.class, Level.SEVERE, ex1.toString(), ex1);
      }
   }

   private boolean isLocalFileAvailable(IFile file) {
      boolean found = false;
      if (file != null) {
         try {
            IFileStore store = EFS.getStore(file.getLocationURI());
            found = store.fetchInfo().exists();
         } catch (Exception ex) {
            OseeLog.log(OteDefinePlugin.class, Level.SEVERE, "Error retrieving file system. ", ex);
         }
      }
      return found;
   }

   public IFile getResults() {
      return remoteFile;
   }

   protected HttpURLConnection setupConnection(URL url) throws IOException {
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      connection.setReadTimeout(CONNECTION_READ_TIMEOUT);
      return connection;
   }

   public int getTotalWork() throws Exception {
      return isLocalFileAvailable(getLocalStorageIFile()) != true ? 4 : 1;
   }

   private boolean isRemoteRequestNeeded() throws Exception {
      return isLocalFileAvailable(getLocalStorageIFile()) != true;
   }

   public void execute(IProgressMonitor monitor) throws Exception {
      if (isRemoteRequestNeeded() != false) {
         HttpURLConnection connection = null;
         try {
            monitor.setTaskName(String.format("Requesting Resource: [%s]", getRemoteFileName()));
            connection = setupConnection(new URL(remoteRequestUrl));
            connection.connect();
            monitor.worked(1);
            if (monitor.isCanceled() != true) {
               this.remoteFile = handleTransfer(monitor, connection);
               monitor.worked(1);
            }
         } finally {
            if (connection != null) {
               connection.disconnect();
            }
         }
      } else {
         this.remoteFile = getLocalStorageIFile();
      }
      monitor.worked(1);
   }

   protected IFile getLocalStorageIFile() throws Exception {
      return OseeData.getIFile(getLocalStorageName());
   }

   protected String getRemoteRequestUrl() {
      return remoteRequestUrl;
   }

   protected String getTempFolderName() {
      return tempFolderName;
   }

   protected abstract String getLocalStorageName() throws Exception;

   protected abstract String getRemoteFileName();

   protected abstract IFile handleTransfer(IProgressMonitor monitor, HttpURLConnection connection) throws Exception;

}