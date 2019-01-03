/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AbstractAtsAction extends Action {

   public AbstractAtsAction(String string, ImageDescriptor imageDescriptor) {
      super(string, imageDescriptor);
   }

   public AbstractAtsAction(String string) {
      super(string);
   }

   public AbstractAtsAction() {
      super();
   }

   public AbstractAtsAction(String string, int asPushButton) {
      super(string, asPushButton);
   }

   public void runWithException() throws Exception {
      // provided for subclass implementation
   }

   @Override
   public void run() {
      try {
         runWithException();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

}
