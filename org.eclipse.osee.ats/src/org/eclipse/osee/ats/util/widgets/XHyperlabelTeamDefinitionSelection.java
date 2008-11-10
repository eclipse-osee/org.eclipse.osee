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
package org.eclipse.osee.ats.util.widgets;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelTeamDefinitionSelection extends XHyperlinkLabelSelection {

   public static final String WIDGET_ID = XHyperlabelTeamDefinitionSelection.class.getSimpleName();
   Set<TeamDefinitionArtifact> selectedTeamDefs = new HashSet<TeamDefinitionArtifact>();

   /**
    * @param label
    */
   public XHyperlabelTeamDefinitionSelection(String label) {
      super(label);
   }

   public Set<TeamDefinitionArtifact> getSelectedTeamDefintions() {
      return selectedTeamDefs;
   }

   @Override
   public String getCurrentValue() {
      StringBuffer sb = new StringBuffer();
      for (TeamDefinitionArtifact user : selectedTeamDefs)
         sb.append(user.getDescriptiveName() + ", ");
      return sb.toString().replaceFirst(", $", "");
   }

   public void setSelectedUsers(Set<TeamDefinitionArtifact> selectedUsers) {
      this.selectedTeamDefs = selectedUsers;
      refresh();
   }

   @Override
   public boolean handleSelection() {
      try {
         TeamDefinitionTreeWithChildrenDialog dialog = new TeamDefinitionTreeWithChildrenDialog(Active.Active);
         int result = dialog.open();
         if (result == 0) {
            selectedTeamDefs.clear();
            for (Object obj : dialog.getResultAndRecursedTeamDefs()) {
               selectedTeamDefs.add((TeamDefinitionArtifact) obj);
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
