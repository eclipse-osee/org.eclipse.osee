/*
 * Created on Mar 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.utility;

import java.io.BufferedInputStream;
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
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class UriResourceContentFinder {

   private final URI source;
   private final HashCollection<IResourceLocator, IResourceHandler> locatorMap;
   private final boolean isRecursionAllowed;

   public UriResourceContentFinder(final URI source, final boolean isRecursionAllowed) {
      super();
      this.source = source;
      this.isRecursionAllowed = isRecursionAllowed;
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
         processFileStore(monitor, EFS.getStore(source));
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
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

   private void processDirectory(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      boolean isProcessingAllowed = false;
      for (IFileStore childStore : fileStore.childStores(EFS.NONE, monitor)) {
         System.out.println(childStore.getName());
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

   private void processFile(IProgressMonitor monitor, IFileStore fileStore) throws Exception {
      if (!monitor.isCanceled()) {
         for (IResourceLocator locator : locatorMap.keySet()) {
            if (locator.isValidFileStore(fileStore)) {
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
