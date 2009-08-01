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
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;

/**
 * @author Ryan D. Brooks
 */
public abstract class CommandHandler extends AbstractHandler {

   @Override
   public boolean isEnabled() {
      try {
         return isEnabledWithException();
      } catch (OseeCoreException ex) {
         OseeLog.log(OseePluginUiActivator.class, Level.SEVERE, ex);
         return false;
      }
   }

   public abstract boolean isEnabledWithException() throws OseeCoreException;
}
