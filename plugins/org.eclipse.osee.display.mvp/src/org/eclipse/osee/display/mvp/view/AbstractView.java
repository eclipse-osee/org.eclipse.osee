/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp.view;

import org.eclipse.osee.display.mvp.MessageType;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractView implements View {

   private Log logger;
   private boolean isDisposed = false;

   @Override
   public Log getLogger() {
      return logger;
   }

   @Override
   public void setLogger(Log logger) {
      this.logger = logger;
   }

   @Override
   public final void dispose() {
      if (!isDisposed()) {
         isDisposed = true;
         onDispose();
      }
   }

   @Override
   public boolean isDisposed() {
      return isDisposed;
   }

   @Override
   public void displayMessage(String caption) {
      //
   }

   @Override
   public void displayMessage(String caption, String description, MessageType messageType) {
      //
   }

   protected abstract void onDispose();
}
