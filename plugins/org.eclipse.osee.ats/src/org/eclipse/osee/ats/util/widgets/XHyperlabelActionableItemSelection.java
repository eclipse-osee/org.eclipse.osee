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
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.dialog.ActionableItemTreeWithChildrenDialog;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelActionableItemSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelActionableItemSelection.class.getSimpleName();
   Collection<ActionableItemArtifact> selectedAis = new HashSet<ActionableItemArtifact>();
   Collection<ActionableItemArtifact> teamDefs;
   ActionableItemTreeWithChildrenDialog dialog = null;

   public XHyperlabelActionableItemSelection(String label) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public Collection<ActionableItemArtifact> getSelectedTeamDefintions() {
      return selectedAis;
   }

   @Override
   public String getCurrentValue() {
      return Artifacts.commaArts(selectedAis);
   }

   public void setSelectedAIs(Collection<ActionableItemArtifact> selectedTeamDefs) {
      this.selectedAis = selectedTeamDefs;
      refresh();
      notifyXModifiedListeners();
   }

   @Override
   public boolean handleClear() {
      selectedAis.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (teamDefs == null) {
            dialog = new ActionableItemTreeWithChildrenDialog(Active.Both);
         } else {
            dialog = new ActionableItemTreeWithChildrenDialog(Active.Both, teamDefs);
         }
         int result = dialog.open();
         if (result == 0) {
            selectedAis.clear();
            for (Object obj : dialog.getResultAndRecursedAIs()) {
               selectedAis.add((ActionableItemArtifact) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setTeamDefs(Collection<ActionableItemArtifact> teamDefs) {
      this.teamDefs = teamDefs;
      if (dialog != null) {
         dialog.setInput(teamDefs);
      }
   }

   @Override
   public boolean isEmpty() {
      return selectedAis.isEmpty();
   }

}
