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

package org.eclipse.osee.ats.navigate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.editor.TaskEditorInput;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class EditTasksBySelectedWorkflows extends XNavigateItemAction {

   /**
    * @param parent
    */
   public EditTasksBySelectedWorkflows(XNavigateItem parent) {
      super(parent, "Edit Tasks by Selected Workflows");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions)throws OseeCoreException, SQLException{
      Set<StateMachineArtifact> smas = WorldView.getWorldView().getxViewer().getSelectedSMAArtifacts();
      if (smas.size() == 0) {
         AWorkbench.popup("ERROR", "Must select Action Workflows in ATS World to edit tasks.");
         return;
      }
      TeamDefinitionArtifact teamDef = null;
      List<TaskArtifact> taskArts = new ArrayList<TaskArtifact>();
      for (StateMachineArtifact sma : smas) {
         if (!(sma instanceof TeamWorkFlowArtifact)) {
            AWorkbench.popup("ERROR", "Selected item not a Team Workflow: " + sma);
            return;
         }
         if (teamDef == null)
            teamDef = ((TeamWorkFlowArtifact) sma).getTeamDefinition();
         else if (teamDef != ((TeamWorkFlowArtifact) sma).getTeamDefinition()) {
            AWorkbench.popup("ERROR",
                  "All Team Workflows must belong to same Team Definition.  Invalid Workflow: " + sma);
            return;
         }
         taskArts.addAll(sma.getSmaMgr().getTaskMgr().getTaskArtifacts());
      }
      if (taskArts.size() == 0) {
         AWorkbench.popup("ERROR", "No tasks associated with selected workflows.");
         return;
      }
      TaskEditor.editArtifacts(new TaskEditorInput("Tasks from selected workflows.", taskArts));
   }
}
