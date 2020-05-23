/*********************************************************************
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.doors.connector.ui.handler;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osee.doors.connector.ui.perspectives.Doors;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Command handler to refresh the browser view
 * 
 * @author Chandan Bandemutt
 */
public class RefreshAction implements IViewActionDelegate {

   @Override
   public void run(final IAction action) {
      IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
         "org.eclipse.osee.doors.connector.ui.Doors");

      ((Doors) view).refresh();
   }

   @Override
   public void selectionChanged(final IAction action, final ISelection selection) {
      //
   }

   @Override
   public void init(final IViewPart view) {
      //
   }

}
