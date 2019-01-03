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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionTreeWithChildrenDialog;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelTeamDefinitionSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelTeamDefinitionSelection.class.getSimpleName();
   Collection<IAtsTeamDefinition> selectedTeamDefs = new HashSet<>();
   Collection<IAtsTeamDefinition> teamDefs;
   TeamDefinitionTreeWithChildrenDialog dialog = null;

   public XHyperlabelTeamDefinitionSelection(String label) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public Collection<IAtsTeamDefinition> getSelectedTeamDefintions() {
      return selectedTeamDefs;
   }

   @Override
   public Object getData() {
      List<Artifact> arts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getSelectedTeamDefintions());
      return arts;
   }

   @Override
   public String getCurrentValue() {
      return Artifacts.commaArts(selectedTeamDefs);
   }

   public void setSelectedTeamDefs(Collection<IAtsTeamDefinition> selectedTeamDefs) {
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
               selectedTeamDefs.add((IAtsTeamDefinition) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setTeamDefs(Collection<IAtsTeamDefinition> teamDefs) {
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
