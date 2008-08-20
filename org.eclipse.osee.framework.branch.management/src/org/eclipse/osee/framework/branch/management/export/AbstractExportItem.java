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
package org.eclipse.osee.framework.branch.management.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractExportItem implements Runnable {
   private static final String XML_EXTENSION = ".xml";

   private String name;
   private int priority;
   private File writeLocation;
   private Options options;
   private Set<IExportListener> exportListeners;

   private boolean cancel;

   public AbstractExportItem(String name, int priority) {
      this.name = name;
      this.priority = priority;
      this.options = null;
      this.cancel = false;
      this.exportListeners = Collections.synchronizedSet(new HashSet<IExportListener>());
   }

   public String getName() {
      return name;
   }

   public int getPriority() {
      return priority;
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

   public void addExportListener(IExportListener exportListener) {
      if (exportListener != null) {
         this.exportListeners.add(exportListener);
      }
   }

   public void removeExportListener(IExportListener exportListener) {
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

   private Writer createXmlWriter(File tempFolder, String name, int bufferSize) throws Exception {
      File indexFile = new File(tempFolder, name);
      Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), "UTF-8"), bufferSize);
      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
      return writer;
   }

   public final void run() {
      notifyOnExportItemStarted();
      long startTime = System.currentTimeMillis();
      Writer writer = null;
      try {
         writer = createXmlWriter(getWriteLocation(), getName() + XML_EXTENSION, getBufferSize());
         writer.write("<data >\n");
         if (isCancel() != true) {
            doWork(writer);
         }
         writer.write("</data>\n");
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
      for (IExportListener listener : this.exportListeners) {
         listener.onException(getName(), ex);
      }
   }

   protected void notifyOnExportItemStarted() {
      for (IExportListener listener : this.exportListeners) {
         listener.onExportItemStarted(getName());
      }
   }

   protected void notifyOnExportItemCompleted(long timeToProcess) {
      for (IExportListener listener : this.exportListeners) {
         listener.onExportItemCompleted(getName(), timeToProcess);
      }
   }

   protected abstract void doWork(Writer writer) throws Exception;

   public void setCancel(boolean cancel) {
      this.cancel = cancel;
   }

   public boolean isCancel() {
      return this.cancel;
   }
}
