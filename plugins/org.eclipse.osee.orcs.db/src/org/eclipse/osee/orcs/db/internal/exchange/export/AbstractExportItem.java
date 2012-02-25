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
package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.orcs.db.internal.exchange.IExchangeTaskListener;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractExportItem implements Callable<Boolean> {
   private final ExportItem id;
   private final String fileName;
   private final Set<IExchangeTaskListener> exportListeners;

   private File writeLocation;
   private boolean cancel;

   public AbstractExportItem(ExportItem id) {
      this.id = id;
      this.fileName = id.getFileName();
      this.cancel = false;
      this.exportListeners = Collections.synchronizedSet(new HashSet<IExchangeTaskListener>());
   }

   public String getSource() {
      return id.getSource();
   }

   public String getFileName() {
      return fileName;
   }

   public String getName() {
      return id.toString();
   }

   public int getPriority() {
      return id.getPriority();
   }

   public int getBufferSize() {
      return (int) Math.pow(2, 20);
   }

   public void setWriteLocation(File writeLocation) {
      this.writeLocation = writeLocation;
   }

   public File getWriteLocation() {
      return writeLocation;
   }

   public void addExportListener(IExchangeTaskListener exportListener) {
      if (exportListener != null) {
         this.exportListeners.add(exportListener);
      }
   }

   public void removeExportListener(IExchangeTaskListener exportListener) {
      if (exportListener != null) {
         this.exportListeners.remove(exportListener);
      }
   }

   public void cleanUp() {
      this.setWriteLocation(null);
      this.exportListeners.clear();
   }

   @Override
   public final Boolean call() throws Exception {
      boolean wasSuccessful = false;
      long startTime = System.currentTimeMillis();
      try {
         if (!isCancel()) {
            executeWork();
         }
         wasSuccessful = true;
      } catch (Exception ex) {
         notifyOnExportException(ex);
         throw ex;
      } finally {
         notifyOnExportItemCompleted(System.currentTimeMillis() - startTime);
      }
      return wasSuccessful;
   }

   protected void notifyOnExportException(Throwable ex) {
      for (IExchangeTaskListener listener : this.exportListeners) {
         listener.onException(getName(), ex);
      }
   }

   protected void notifyOnExportItemCompleted(long timeToProcess) {
      for (IExchangeTaskListener listener : this.exportListeners) {
         listener.onExportItemCompleted(getName(), timeToProcess);
      }
   }

   protected abstract void executeWork() throws Exception;

   public void setCancel(boolean cancel) {
      this.cancel = cancel;
   }

   public boolean isCancel() {
      return this.cancel;
   }
}
