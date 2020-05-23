/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public abstract class AbstractSelectionChangedHandler extends CommandHandler {
   private final HandlerEvent enabledChangedEvent = new HandlerEvent(this, true, false);
   private ViewerMenuDetectListener viewerMenuDetectListener;

   public AbstractSelectionChangedHandler() {
      addlistener();
   }
   private class ViewerMenuDetectListener implements MenuDetectListener {
      @Override
      public void menuDetected(MenuDetectEvent e) {
         fireHandlerChanged(enabledChangedEvent);
      }
   }

   private void addlistener() {
      if (PlatformUI.isWorkbenchRunning()) {
         IWorkbench workbench = PlatformUI.getWorkbench();
         if (workbench != null && !workbench.isStarting() && !workbench.isClosing()) {
            viewerMenuDetectListener = new ViewerMenuDetectListener();
            IWorkbenchPart workbenchPart = AWorkbench.getActivePage().getActivePart();
            Object object = workbenchPart.getSite().getSelectionProvider();

            if (object instanceof Viewer) {
               ((Viewer) object).getControl().addMenuDetectListener(viewerMenuDetectListener);
            }
         }
      }
   }
}
