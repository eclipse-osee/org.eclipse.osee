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
package org.eclipse.osee.display.mvp.mocks;

import org.eclipse.osee.display.mvp.MessageType;
import org.eclipse.osee.display.mvp.view.AbstractView;

/**
 * @author Roberto E. Escobar
 */
public class View2 extends AbstractView {

   @Override
   public void displayMessage(String caption) {
      //
   }

   @Override
   public void displayMessage(String caption, String description, MessageType type) {
      //
   }

   @Override
   protected void onDispose() {
      //
   }

   @Override
   public Object getContent() {
      return null;
   }
}
