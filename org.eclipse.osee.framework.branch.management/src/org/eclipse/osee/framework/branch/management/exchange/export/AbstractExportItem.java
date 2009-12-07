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
package org.eclipse.osee.framework.branch.management.exchange.export;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.branch.management.IExchangeTaskListener;
import org.eclipse.osee.framework.branch.management.exchange.ExchangeUtil;
import org.eclipse.osee.framework.branch.management.exchange.ExportImportXml;
import org.eclipse.osee.framework.branch.management.exchange.handler.ExportItem;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractExportItem implements Runnable {
   private final ExportItem id;
   private final String fileName;
   private final Set<IExchangeTaskListener> exportListeners;

   private File writeLocation;
   private Options options;
   private boolean cancel;

   public AbstractExportItem(ExportItem id) {
      this.id = id;
      this.fileName = id.toString() + ExportImportXml.XML_EXTENSION;
      this.options = null;
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

   public void setOptions(Options options) {
      this.options = options;
   }

   public Options getOptions() {
      return this.options;
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
      if (this.options != null) {
         this.options.clear();
      }
      this.exportListeners.clear();
   }

   public final void run() {
      notifyOnExportItemStarted();
      long startTime = System.currentTimeMillis();
      Writer writer = null;
      try {
         writer = ExchangeUtil.createXmlWriter(getWriteLocation(), getFileName(), getBufferSize());
         ExportImportXml.openXmlNode(writer, ExportImportXml.DATA);
         if (isCancel() != true) {
            try {
               doWork(writer);
            } catch (Exception ex) {
               notifyOnExportException(ex);
            }
         }
         ExportImportXml.closeXmlNode(writer, ExportImportXml.DATA);
      } catch (Exception ex) {
         notifyOnExportException(ex);
      } finally {
         if (writer != null) {
            try {
               writer.close();
            } catch (IOException ex) {
               notifyOnExportException(ex);
            }
         }
         notifyOnExportItemCompleted(System.currentTimeMillis() - startTime);
      }
   }

   protected void notifyOnExportException(Throwable ex) {
      for (IExchangeTaskListener listener : this.exportListeners) {
         listener.onException(getName(), ex);
      }
   }

   protected void notifyOnExportItemStarted() {
      for (IExchangeTaskListener listener : this.exportListeners) {
         listener.onExportItemStarted(getName());
      }
   }

   protected void notifyOnExportItemCompleted(long timeToProcess) {
      for (IExchangeTaskListener listener : this.exportListeners) {
         listener.onExportItemCompleted(getName(), timeToProcess);
      }
   }

   protected abstract void doWork(Appendable appendable) throws Exception;

   public void setCancel(boolean cancel) {
      this.cancel = cancel;
   }

   public boolean isCancel() {
      return this.cancel;
   }
}
