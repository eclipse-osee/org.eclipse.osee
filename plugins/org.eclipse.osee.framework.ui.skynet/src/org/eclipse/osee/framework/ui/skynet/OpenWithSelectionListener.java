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
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */
public class OpenWithSelectionListener extends SelectionAdapter {
   private final Command command;
   private final IHandlerService handlerService;

   public OpenWithSelectionListener(Command command) {
      super();
      this.command = command;
      this.handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
   }

   @Override
   public void widgetSelected(SelectionEvent e) {
      try {
         handlerService.executeCommand(command.getId(), null);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

}
