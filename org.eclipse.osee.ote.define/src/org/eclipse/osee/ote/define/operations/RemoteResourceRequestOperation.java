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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Roberto E. Escobar
 */
public class RemoteResourceRequestOperation extends AbstractRemoteResourceRequestTemplate {
   private static final String STORAGE_PATH = "TEMP";
   private String fileName;

   public RemoteResourceRequestOperation(String tempFolderName, String remoteRequestUrl, String fileName) {
      super(STORAGE_PATH, remoteRequestUrl);
      this.fileName = fileName;
   }

   @Override
   protected String getLocalStorageName() throws Exception {
      StringBuilder builder = new StringBuilder();
      builder.append(getTempFolderName());
      builder.append(File.separator);
      builder.append(fileName);
      return builder.toString();
   }

   @Override
   protected String getRemoteFileName() {
      return fileName;
   }

   protected HttpURLConnection setupConnection(URL url) throws IOException {
      HttpURLConnection connection = super.setupConnection(url);
      connection.setAllowUserInteraction(false);
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setInstanceFollowRedirects(true);
      return connection;
   }

   @Override
   protected IFile handleTransfer(IProgressMonitor monitor, HttpURLConnection connection) throws Exception {
      IFile file = null;
      InputStream stream = null;
      try {
         int result = connection.getResponseCode();
         if (result == 200) {
            stream = (InputStream) connection.getContent();
            file = getLocalStorageIFile();
            file.create(stream, true, monitor);
            monitor.setTaskName("File Transfer Complete.");
         }
      } finally {
         if (stream != null) {
            stream.close();
         }
      }
      return file;
   }
}
