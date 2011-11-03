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
public interface View {

   Log getLogger();

   void setLogger(Log logger);

   void displayMessage(String caption);

   void displayMessage(String caption, String description, MessageType messageType);

   void dispose();

   boolean isDisposed();

   Object getContent();
}
