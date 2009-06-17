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
package org.eclipse.osee.ote.define.parser;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Roberto E. Escobar
 */
public abstract class BaseOutfileParser {
   private Set<IDataListener> listeners;

   public BaseOutfileParser() {
      this.listeners = new HashSet<IDataListener>();
   }

   public void registerListener(IDataListener listener) {
      listeners.add(listener);
   }

   public void deregisterListener(IDataListener listener) {
      listeners.remove(listener);
   }

   protected Set<IDataListener> getDataListeners() {
      return listeners;
   }

   protected void notifyOnDataEvent(String name, String value) {
      for (IDataListener listener : listeners) {
         listener.notifyDataEvent(name, value);
      }
   }

   public final void execute(IProgressMonitor monitor, URL fileToParse) throws Exception {
      long time = System.currentTimeMillis();
      String file = new File(fileToParse.toURI()).getName();
      monitor.subTask(String.format("Parsing: [%s]", file));
      InputStream inputStream = null;
      try {
         inputStream = fileToParse.openStream();
         doParse(monitor, file, inputStream);
      } finally {
         try {
            if (inputStream != null) {
               inputStream.close();
            }
         } finally {
            long elapsedTime = System.currentTimeMillis() - time;
            monitor.subTask(String.format("Parsed: [%s] in [%d] ms", file, elapsedTime));
         }
      }
   }

   public abstract boolean isValidParser(URL fileToParse);

   protected abstract void doParse(IProgressMonitor monitor, String fileName, InputStream inputStream) throws Exception;
}
