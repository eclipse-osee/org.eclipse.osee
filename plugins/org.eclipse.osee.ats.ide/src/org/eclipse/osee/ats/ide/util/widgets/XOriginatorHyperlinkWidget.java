/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;


import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserListDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class XOriginatorHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   AtsUser originator;

   public XOriginatorHyperlinkWidget() {
      super("Originator", true, 50);
   }

   @Override
   public String getCurrentValue() {
      return originator == null ? "" : originator.getName();
   }

   @Override
   public boolean handleSelection() {
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select Originator",
         AtsClientService.get().getUserServiceClient().getOseeUsersSorted(Active.Active));
      int result = ld.open();
      if (result == 0) {
         originator = AtsClientService.get().getUserServiceClient().getUserFromOseeUser(ld.getSelection());
         return true;
      }
      return false;
   }

   @Override
   public boolean handleClear() {
      originator = null;
      return true;
   }

   public AtsUser getSelected() {
      return originator;
   }

}
