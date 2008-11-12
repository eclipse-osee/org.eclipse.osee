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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.GroupListDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelGroupSelection extends XHyperlinkLabelSelection {

   public static final String WIDGET_ID = XHyperlabelGroupSelection.class.getSimpleName();
   Set<Artifact> selectedGroups = new HashSet<Artifact>();

   /**
    * @param label
    */
   public XHyperlabelGroupSelection(String label) {
      super(label);
   }

   public Set<Artifact> getSelectedGroups() {
      return selectedGroups;
   }

   @Override
   public String getCurrentValue() {
      StringBuffer sb = new StringBuffer();
      for (Artifact user : selectedGroups)
         sb.append(user.getDescriptiveName() + ", ");
      return sb.toString().replaceFirst(", $", "");
   }

   public void setSelectedUsers(Set<Artifact> selectedUsers) {
      this.selectedGroups = selectedUsers;
      refresh();
   }

   @Override
   public boolean handleSelection() {
      try {
         GroupListDialog dialog = new GroupListDialog(Display.getCurrent().getActiveShell());
         dialog.setRequireSelection(false);
         int result = dialog.open();
         if (result == 0) {
            selectedGroups.clear();
            for (Object obj : dialog.getResult()) {
               selectedGroups.add((Artifact) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return false;
   }

}
