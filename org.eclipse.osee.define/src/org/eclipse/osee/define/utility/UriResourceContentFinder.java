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
package org.eclipse.osee.define.utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UriResourceContentFinder {

   private final URI source;
   private final HashCollection<IResourceLocator, IResourceHandler> locatorMap;
   private final boolean isRecursionAllowed;
   private final boolean isFileWithMultiplePaths;

   public UriResourceContentFinder(final URI source, final boolean isRecursionAllowed, final boolean isFileWithMultiplePaths) {
      super();
      this.source = source;
      this.isRecursionAllowed = isRecursionAllowed;
      this.isFileWithMultiplePaths = isFileWithMultiplePaths;
      this.locatorMap = new HashCollection<IResourceLocator, IResourceHandler>();
   }

   public void addLocator(IResourceLocator locator, IResourceHandler... handler) {
      if (locator != null && handler != null && handler.length > 0) {
         synchronized (locatorMap) {
            locatorMap.put(locator, Arrays.asList(handler));
         }
      }
   }

   public void removeLocator(IResourceLocator locator) {
      if (locator != null) {
         synchronized (locatorMap) {
            locatorMap.removeValues(locator);
         }
      }
   }

   public void execute(IProgressMonitor monitor) throws OseeCoreException {
      try {
         IFileStore fileStore = EFS.getStore(source);
         if (isFileWithMultiplePaths) {
            processFileWithPaths(monitor, fileStore);
         } else {
            processFileStore(monitor, fileStore);
         }
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void processFileWithPaths(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      IFileInfo info = fileStore.fetchInfo(EFS.NONE, monitor);
      if (info != null && info.exists()) {
         for (String path : Lib.readListFromFile(new File(fileStore.toURI()), true)) {
            if (Strings.isValid(path)) {
               processFileStore(monitor, EFS.getStore(new File(path).toURI()));
            }
            if (monitor.isCanceled()) {
               break;
            }
         }
      }
   }

   private void processFileStore(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      IFileInfo info = fileStore.fetchInfo(EFS.NONE, monitor);
      if (info != null && info.exists()) {
         if (info.isDirectory()) {
            processDirectory(monitor, fileStore);
         } else {
            processFile(monitor, fileStore);
         }
      }
   }

   private boolean isValidDirectory(IProgressMonitor monitor, IFileStore fileStore) {
      boolean result = false;
      for (IResourceLocator locator : locatorMap.keySet()) {
         if (monitor.isCanceled()) {
            break;
         }
         if (locator.isValidDirectory(fileStore)) {
            result = true;
            break;
         }
      }
      return result;
   }

   private void processDirectory(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      if (isValidDirectory(monitor, fileStore)) {
         boolean isProcessingAllowed = false;
         for (IFileStore childStore : fileStore.childStores(EFS.NONE, monitor)) {
            isProcessingAllowed = false;
            if (monitor.isCanceled()) {
               break;
            }
            if (!isRecursionAllowed) {
               isProcessingAllowed = !childStore.fetchInfo().isDirectory();
            } else {
               isProcessingAllowed = true;
            }

            if (isProcessingAllowed) {
               System.out.println(childStore.toURI().toASCIIString() + " - " + childStore.getName());
               processFileStore(monitor, childStore);
            }
         }
      }
   }

   private void processFile(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      if (!monitor.isCanceled()) {
         for (IResourceLocator locator : locatorMap.keySet()) {
            if (locator.isValidFile(fileStore)) {
               monitor.subTask(String.format("Checking: [%s]", fileStore.getName()));
               CharBuffer fileBuffer = getContents(monitor, fileStore);
               if (locator.hasValidContent(fileBuffer)) {
                  String fileName = locator.getIdentifier(fileStore, fileBuffer);
                  if (!monitor.isCanceled()) {
                     notifyListeners(locator, fileStore.toURI(), fileName, fileBuffer);
                  }
               }
            }
         }
      }
      monitor.worked(1);
   }

   private CharBuffer getContents(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      CharBuffer toReturn = null;
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(fileStore.openInputStream(EFS.NONE, monitor));
         toReturn = Lib.inputStreamToCharBuffer(inputStream);
      } finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (IOException ex) {
               OseeLog.log(DefinePlugin.class, Level.SEVERE, String.format("Error closing stream for resource: [%s]",
                     fileStore.getName()), ex);
            }
         }
      }
      return toReturn;
   }

   private void notifyListeners(final IResourceLocator locator, final URI uriPath, final String fileName, final CharBuffer fileBuffer) {
      for (IResourceHandler handler : locatorMap.getValues(locator)) {
         handler.onResourceFound(uriPath, fileName, fileBuffer);
      }
   }
}
