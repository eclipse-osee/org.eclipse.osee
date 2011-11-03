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
public interface Presenter<V extends View, E extends EventBus> {

   Log getLogger();

   void setLogger(Log logger);

   void setView(V view);

   V getView();

   void setEventBus(E eventBus);

   E getEventBus();

   void bind() throws BindException;

   void showNotification(String caption);

   void showNotification(String caption, String description, MessageType type);
}
