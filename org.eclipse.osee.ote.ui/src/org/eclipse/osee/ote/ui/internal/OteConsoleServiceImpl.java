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
package org.eclipse.osee.ote.ui.internal;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;
import org.eclipse.osee.framework.ui.plugin.util.OseeConsole;
import org.eclipse.osee.ote.ui.IOteConsoleService;

/**
 * @author Roberto E. Escobar
 */
public class OteConsoleServiceImpl implements IOteConsoleService {

   private OseeConsole console;

   public OteConsoleServiceImpl() {
      this.console = null;
   }

   private synchronized void ensureCreated() {
      if (console == null) {
         console = new OseeConsole("OTE Console");
         console.popup();
      }
   }

   private OseeConsole getConsole() {
      ensureCreated();
      return console;
   }

   @Override
   public void addInputListener(IConsoleInputListener listener) {
      if (listener != null) {
         getConsole().addInputListener(listener);
      }
   }

   @Override
   public void removeInputListener(IConsoleInputListener listener) {
      if (listener != null) {
         getConsole().removeInputListener(listener);
      }
   }

   @Override
   public void write(String value) {
      getConsole().write(value);
   }

   @Override
   public void writeError(String value) {
      getConsole().writeError(value);
   }

   @Override
   public void prompt(String value) throws IOException {
      getConsole().prompt(value);
   }

   @Override
   public void popup() {
      getConsole().popup();
   }

   @Override
   public void write(String value, int type, boolean popup) {
      getConsole().write(value, type, popup);
   }

   public void close() {
	   getConsole().shutdown();
   }
}
