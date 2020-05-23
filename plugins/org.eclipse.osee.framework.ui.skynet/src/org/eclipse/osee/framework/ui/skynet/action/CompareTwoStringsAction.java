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

package org.eclipse.osee.framework.ui.skynet.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryEntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class CompareTwoStringsAction extends Action {

   public CompareTwoStringsAction() {
      setText("Compare Two Strings");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         final EntryEntryDialog ed =
            new EntryEntryDialog(getText(), "Enter Strings to Compare", "String 1", "String 2");
         ed.setModeless();
         ed.setFillVertically(true);
         ed.setOkListener(new Listener() {

            @Override
            public void handleEvent(Event event) {
               CompareHandler compareHandler =
                  new CompareHandler(null, new CompareItem("First", ed.getEntry(), System.currentTimeMillis(), null),
                     new CompareItem("Second", ed.getEntry2(), System.currentTimeMillis(), null), null);
               compareHandler.compare();
            }
         });
         ed.open();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EDIT);
   }

}
