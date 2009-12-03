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
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class CompareTwoStringsAction extends Action {

   public CompareTwoStringsAction() {
      setText("Compare Two Strings - Compare Editor");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         EntryDialog ed = new EntryDialog(getText(), "Enter First String");
         ed.setFillVertically(true);
         if (ed.open() != 0) return;
         String firstStr = ed.getEntry();
         ed = new EntryDialog(getText(), "Enter Second String");
         ed.setFillVertically(true);
         if (ed.open() != 0) return;
         String secondStr = ed.getEntry();
         CompareHandler compareHandler =
               new CompareHandler(new CompareItem("First", firstStr, System.currentTimeMillis()), new CompareItem(
                     "Second", secondStr, System.currentTimeMillis()), null);
         compareHandler.compare();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EDIT);
   }

}
