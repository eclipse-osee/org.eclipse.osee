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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelTeamDefinitionSelection extends XHyperlinkLabelCmdValueSelection {

   Collection<TeamDefinition> selectedTeamDefs = new HashSet<>();
   Collection<TeamDefinition> teamDefs;
   TeamDefinitionTreeWithChildrenDialog dialog = null;

   public XHyperlabelTeamDefinitionSelection(String label) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public Collection<TeamDefinition> getSelectedTeamDefintions() {
      return selectedTeamDefs;
   }

   @Override
   public Object getData() {
      List<Artifact> arts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getSelectedTeamDefintions());
      return arts;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(",", selectedTeamDefs);
   }

   public void setSelectedTeamDefs(Collection<TeamDefinition> selectedTeamDefs) {
      this.selectedTeamDefs = selectedTeamDefs;
      refresh();
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
            dialog = new TeamDefinitionTreeWithChildrenDialog(Active.Both);
         } else {
            dialog = new TeamDefinitionTreeWithChildrenDialog(Active.Both, teamDefs);
         }
         int result = dialog.open();
         if (result == 0) {
            selectedTeamDefs.clear();
            for (Object obj : dialog.getResultAndRecursedTeamDefs()) {
               selectedTeamDefs.add((TeamDefinition) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setTeamDefs(Collection<TeamDefinition> teamDefs) {
      this.teamDefs = teamDefs;
      if (dialog != null) {
         dialog.setInput(teamDefs);
      }
   }

   @Override
   public boolean isEmpty() {
      return selectedTeamDefs.isEmpty();
   }

}
