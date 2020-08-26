/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
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
      UserListDialog ld = new UserListDialog(Displays.getActiveShell(), "Select Originator", Active.Active);
      int result = ld.open();
      if (result == 0) {
         originator = AtsApiService.get().getUserService().getUserById(ld.getSelection());
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
