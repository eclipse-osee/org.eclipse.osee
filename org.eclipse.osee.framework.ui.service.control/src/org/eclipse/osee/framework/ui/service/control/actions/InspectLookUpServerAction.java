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
package org.eclipse.osee.framework.ui.service.control.actions;

import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.ui.service.control.dialogs.InspectReggieDialogHelper;
import org.eclipse.osee.framework.ui.service.control.widgets.ManagerMain;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class InspectLookUpServerAction implements IDoubleClickListener {
   private ManagerMain mainWindow;

   public InspectLookUpServerAction(ManagerMain mainWindow) {
      super();
      this.mainWindow = mainWindow;
      this.mainWindow.getLookupViewer().getViewer().addDoubleClickListener(this);
   }

   public void doubleClick(DoubleClickEvent event) {
      ISelection sel = event.getSelection();
      if (!sel.isEmpty()) {
         Object object = ((StructuredSelection) sel).getFirstElement();
         if (object instanceof ServiceRegistrar) {
            Display.getDefault().asyncExec(new InspectReggieDialogHelper(mainWindow, (ServiceRegistrar) object));
         }
      }
   }
}
