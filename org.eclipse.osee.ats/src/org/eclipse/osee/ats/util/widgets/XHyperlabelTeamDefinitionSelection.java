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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelTeamDefinitionSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelTeamDefinitionSelection.class.getSimpleName();
   Collection<TeamDefinitionArtifact> selectedTeamDefs = new HashSet<TeamDefinitionArtifact>();
   Collection<TeamDefinitionArtifact> teamDefs;
   TeamDefinitionTreeWithChildrenDialog dialog = null;

   /**
    * @param label
    */
   public XHyperlabelTeamDefinitionSelection(String label) {
      super(label, true);
   }

   public Collection<TeamDefinitionArtifact> getSelectedTeamDefintions() {
      return selectedTeamDefs;
   }

   @Override
   public String getCurrentValue() {
      return Artifacts.commaArts(selectedTeamDefs);
   }

   public void setSelectedTeamDefs(Collection<TeamDefinitionArtifact> selectedTeamDefs) {
      this.selectedTeamDefs = selectedTeamDefs;
      notifyXModifiedListeners();
   }

   @Override
   public boolean handleClear() {
      selectedTeamDefs.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (teamDefs == null) {
            dialog = new TeamDefinitionTreeWithChildrenDialog(Active.Active);
         } else {
            dialog = new TeamDefinitionTreeWithChildrenDialog(Active.Active, teamDefs);
         }
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
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   /**
    * @param teamDefs the teamDefs to set
    */
   public void setTeamDefs(Collection<TeamDefinitionArtifact> teamDefs) {
      this.teamDefs = teamDefs;
      if (dialog != null) {
         dialog.setInput(teamDefs);
      }
   }

}
