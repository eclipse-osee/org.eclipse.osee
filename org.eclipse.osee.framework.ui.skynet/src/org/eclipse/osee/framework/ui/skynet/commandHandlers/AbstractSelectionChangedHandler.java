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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public abstract class AbstractSelectionChangedHandler extends AbstractHandler {
   private final HandlerEvent enabledChangedEvent = new HandlerEvent(this, true, false);
   private ViewerMenuDetectListener viewerMenuDetectListener;

   public AbstractSelectionChangedHandler() {
      if (!PlatformUI.getWorkbench().isClosing()) {
         viewerMenuDetectListener = new ViewerMenuDetectListener();
         IWorkbenchPart workbenchPart = AWorkbench.getActivePage().getActivePart();
         Object object = workbenchPart.getSite().getSelectionProvider();

         if (object instanceof Viewer) {
            ((Viewer) object).getControl().addMenuDetectListener(viewerMenuDetectListener);
         }
      }
   }
   private class ViewerMenuDetectListener implements MenuDetectListener {
      /* (non-Javadoc)
       * @see org.eclipse.swt.events.MenuDetectListener#menuDetected(org.eclipse.swt.events.MenuDetectEvent)
       */
      public void menuDetected(MenuDetectEvent e) {
         fireHandlerChanged(enabledChangedEvent);
      }
   }
}
