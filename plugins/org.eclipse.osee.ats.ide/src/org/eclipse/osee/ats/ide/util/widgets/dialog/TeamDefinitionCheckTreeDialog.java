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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionCheckTreeDialog extends FilteredCheckboxTreeDialog<IAtsTeamDefinition> {

   private final Active active;
   private List<IAtsTeamDefinition> initialTeamDefs;
   private final boolean requiredSelection;

   public TeamDefinitionCheckTreeDialog(String title, String message, Active active, boolean requiredSelection) {
      super(title, message, new TeamDefinitionTreeContentProvider(active), new AtsObjectLabelProvider(),
         new ArtifactNameSorter());
      this.active = active;
      this.requiredSelection = requiredSelection;
   }

   @Override
   public Collection<IAtsTeamDefinition> getChecked() {
      return super.getTreeViewer().getChecked();
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         List<IAtsTeamDefinition> topLevelTeamDefinitions =
            TeamDefinitions.getTopLevelTeamDefinitions(active, AtsClientService.get().getQueryService());
         getTreeViewer().getViewer().setInput(topLevelTeamDefinitions);
         if (getInitialTeamDefs() != null) {
            getTreeViewer().setInitalChecked(getInitialTeamDefs());

            if (getInitialTeamDefs().size() == 1) {
               getTreeViewer().getCheckboxTreeViewer().expandToLevel(initialTeamDefs.iterator().next(), 1);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      Result result = Result.TrueResult;
      try {
         if (requiredSelection && getChecked().isEmpty()) {
            result = new Result("Must select Team Definition(s)");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return result;
   }

   /**
    * @return the initialTeamDefs
    */
   public List<IAtsTeamDefinition> getInitialTeamDefs() {
      return initialTeamDefs;
   }

   /**
    * @param initialTeamDefs the initialTeamDefs to set
    */
   public void setInitialTeamDefs(List<IAtsTeamDefinition> initialTeamDefs) {
      this.initialTeamDefs = initialTeamDefs;
      if (initialTeamDefs.size() == 1) {
         getTreeViewer().getCheckboxTreeViewer().expandToLevel(initialTeamDefs.iterator().next(), 1);
      }
   }

}
