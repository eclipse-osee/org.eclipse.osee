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
package org.eclipse.osee.ote.ui.markers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class FileWatchList {

   private class FileWatchItem {
      IFile file;
      List<IMarker> markers;
      long timeUpdated;

      FileWatchItem(IFile file, List<IMarker> markers) {
         this.file = file;
         this.markers = markers;
         timeUpdated = System.currentTimeMillis();
      }
   }

   private final List<FileWatchItem> fileWatchItems;

   public FileWatchList() {
      fileWatchItems = new CopyOnWriteArrayList<FileWatchItem>();
   }

   public void put(IFile file, List<IMarker> markers) {
      FileWatchItem item = findWatchItem(file);
      if (item == null) {
         if (isListTooBig()) {
            removeOldestWatchItem();
         }
         fileWatchItems.add(new FileWatchItem(file, markers));
      } else {
         item.markers = markers;
         item.timeUpdated = System.currentTimeMillis();
      }
   }

   /**
    * 
    */
   private void removeOldestWatchItem() {
      FileWatchItem oldest = null;
      for (FileWatchItem item : fileWatchItems) {
         if (oldest == null) {
            oldest = item;
         } else {
            if (oldest.timeUpdated > item.timeUpdated) {
               oldest = item;
            }
         }
      }
      if (oldest != null) {
         OseeLog.log(FileWatchList.class, Level.INFO, String.format(
               "Removing markers from [%s] because maximium marker watch list size has been reached.",
               oldest.file.getName()));
         fileWatchItems.remove(oldest);
         if (oldest.markers != null) {
            for (IMarker marker : oldest.markers) {
               try {
                  marker.delete();
               } catch (CoreException ex) {
                  OseeLog.log(FileWatchList.class, Level.SEVERE, ex);
               }
            }
         }
      }
   }

   /**
    * @return
    */
   private boolean isListTooBig() {
      return fileWatchItems.size() > 20;
   }

   public List<IMarker> get(IFile file) {
      FileWatchItem item = findWatchItem(file);
      if (item != null) {
         return item.markers;
      } else {
         return null;
      }
   }

   /**
    * @param file
    * @return
    */
   private FileWatchItem findWatchItem(IFile file) {
      for (FileWatchItem item : fileWatchItems) {
         if (item.file.equals(file)) {
            return item;
         }
      }
      return null;
   }

}
