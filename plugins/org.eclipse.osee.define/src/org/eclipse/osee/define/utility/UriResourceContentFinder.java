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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UriResourceContentFinder {

   private final Iterable<URI> sources;
   private final HashCollection<IResourceLocator, IResourceHandler> locatorMap;
   private final boolean isRecursionAllowed;
   private final boolean isFileWithMultiplePaths;

   public UriResourceContentFinder(final Iterable<URI> sources, final boolean isRecursionAllowed, final boolean isFileWithMultiplePaths) {
      super();
      this.sources = sources;
      this.isRecursionAllowed = isRecursionAllowed;
      this.isFileWithMultiplePaths = isFileWithMultiplePaths;
      this.locatorMap = new HashCollection<>();
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

   public void execute(IProgressMonitor monitor)  {
      try {
         for (URI source : sources) {
            if (monitor.isCanceled()) {
               break;
            }
            IFileStore fileStore = EFS.getStore(source);
            if (isFileWithMultiplePaths) {
               processFileWithPaths(monitor, fileStore);
            } else {
               monitor.beginTask("Scanning files", 1);
               processFileStore(monitor, fileStore);
               monitor.worked(1);
            }
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         monitor.done();
      }
   }

   private void processFileWithPaths(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      IFileInfo info = fileStore.fetchInfo(EFS.NONE, monitor);
      if (info != null && info.exists()) {
         List<String> paths = Lib.readListFromFile(new File(fileStore.toURI()), true);
         monitor.beginTask("Searching for files", paths.size());
         for (String path : paths) {
            if (Strings.isValid(path)) {
               processFileStore(monitor, EFS.getStore(new File(path).toURI()));
            }
            if (monitor.isCanceled()) {
               break;
            }
            monitor.worked(1);
         }
      } else {
         monitor.beginTask("Searching for files", 1);
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
               processFileStore(monitor, childStore);
            }
         }
      }
      monitor.worked(1);
   }

   private void processFile(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      if (!monitor.isCanceled()) {
         for (IResourceLocator locator : locatorMap.keySet()) {
            if (locator.isValidFile(fileStore)) {
               CharBuffer fileBuffer = getContents(monitor, fileStore);
               if (locator.hasValidContent(fileBuffer)) {
                  String fileName = locator.getIdentifier(fileStore, fileBuffer).getName();
                  if (!monitor.isCanceled()) {
                     monitor.subTask(String.format("processing [%s]", fileStore.getName()));
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
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error closing stream for resource: [%s]",
                  fileStore.getName());
            }
         }
      }
      return toReturn;
   }

   private void notifyListeners(final IResourceLocator locator, final URI uriPath, final String fileName, final CharBuffer fileBuffer)  {
      for (IResourceHandler handler : locatorMap.getValues(locator)) {
         handler.onResourceFound(uriPath, fileName, fileBuffer);
      }
   }
}
