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
package org.eclipse.osee.display.mvp.presenter;

import org.eclipse.osee.display.mvp.BindException;
import org.eclipse.osee.display.mvp.MessageType;
import org.eclipse.osee.display.mvp.event.EventBus;
import org.eclipse.osee.display.mvp.view.View;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractPresenter<V extends View, E extends EventBus> implements Presenter<V, E> {

   private Log logger;

   private V view;
   private E eventBus;

   protected AbstractPresenter() {
      // 
   }

   @Override
   public Log getLogger() {
      return logger;
   }

   @Override
   public void setLogger(Log logger) {
      this.logger = logger;
   }

   @Override
   public void setView(V view) {
      this.view = view;
   }

   @Override
   public V getView() {
      return this.view;
   }

   @Override
   public void setEventBus(E eventBus) {
      this.eventBus = eventBus;
   }

   @Override
   public E getEventBus() {
      return this.eventBus;
   }

   @SuppressWarnings("unused")
   @Override
   public void bind() throws BindException {
      // 
   }

   @Override
   public void showNotification(String caption) {
      view.displayMessage(caption);
   }

   @Override
   public void showNotification(String caption, String description, MessageType type) {
      view.displayMessage(caption, description, type);
   }
}
