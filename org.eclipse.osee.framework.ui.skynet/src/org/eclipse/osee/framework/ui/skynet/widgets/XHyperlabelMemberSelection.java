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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.UserCheckTreeDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelMemberSelection extends XHyperlinkLabelSelection {

   Set<User> selectedUsers = new HashSet<User>();

   /**
    * @param label
    */
   public XHyperlabelMemberSelection(String label) {
      super(label);
   }

   public Set<User> getSelectedUsers() {
      return selectedUsers;
   }

   @Override
   public String getCurrentValue() {
      StringBuffer sb = new StringBuffer();
      for (User user : selectedUsers)
         sb.append(user.getName() + ", ");
      return sb.toString().replaceFirst(", $", "");
   }

   public void setSelectedUsers(Set<User> selectedUsers) {
      this.selectedUsers = selectedUsers;
      refresh();
   }

   @Override
   public boolean handleSelection() {
      try {
         UserCheckTreeDialog uld = new UserCheckTreeDialog(Display.getCurrent().getActiveShell());
         uld.setMessage("Select to assign.\nDeSelect to un-assign.");
         uld.setInitialSelections(selectedUsers);
         if (uld.open() != 0) return false;
         selectedUsers.clear();
         for (Artifact art : uld.getSelection()) {
            selectedUsers.add((User) art);
         }
         return true;
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return false;
   }

}
