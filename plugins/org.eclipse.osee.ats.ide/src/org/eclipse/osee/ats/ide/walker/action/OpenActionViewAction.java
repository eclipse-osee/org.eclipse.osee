/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.walker.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.walker.ActionWalkerView;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class OpenActionViewAction extends Action {

   public OpenActionViewAction() {
      super("Open Action View");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.ACTION));
   }

   @Override
   public void run() {
      try {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ActionWalkerView.VIEW_ID);
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Unable to open outline", ex);
      }
   }

}
