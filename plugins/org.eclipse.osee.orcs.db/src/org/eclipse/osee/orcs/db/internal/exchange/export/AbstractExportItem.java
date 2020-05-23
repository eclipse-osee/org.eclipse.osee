/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.exchange.export;

import java.io.File;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.exchange.handler.ExportItem;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractExportItem extends CancellableCallable<Boolean> {
   private final ExportItem id;
   private final String fileName;

   private File writeLocation;
   private boolean cancel;
   private final Log logger;

   public AbstractExportItem(Log logger, ExportItem id) {
      this.logger = logger;
      this.id = id;
      this.fileName = id.getFileName();
      this.cancel = false;
   }

   protected Log getLogger() {
      return logger;
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

   public void cleanUp() {
      this.setWriteLocation(null);
   }

   @Override
   public final Boolean call() throws Exception {
      long startTime = System.currentTimeMillis();
      try {
         checkForCancelled();
         executeWork();
      } finally {
         getLogger().info("Exported: [%s] in [%s]", getName(), Lib.getElapseString(startTime));
      }
      return true;
   }

   protected abstract void executeWork() throws Exception;

   @Override
   public void setCancel(boolean cancel) {
      this.cancel = cancel;
   }

   public boolean isCancel() {
      return this.cancel;
   }
}
