/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * Abstract XButton widget that will create specified workflow if not already created
 *
 * @author Donald G. Dunne
 */
public abstract class XCreateEscapeWfXButton extends XButton implements ArtifactWidget {

   private TeamWorkFlowArtifact teamWf;
   boolean creating = false;
   protected AtsApi atsApi;

   public XCreateEscapeWfXButton(String name) {
      super(name);
      setImage(ImageManager.getImage(AtsImage.PLAY_GREEN));
      setToolTip(getToolTip());
      addXModifiedListener(listener);
      atsApi = AtsApiService.get();
   }

   /**
    * Override to return other than default tooltip
    */
   @Override
   public String getToolTip() {
      return "Click to Create/Open Escape Analysis Workflow";
   }

   public String getTitle() {
      return "Analyze the Escape for this Change Request";
   }

   abstract public IAtsActionableItem getAi();

   public List<AtsUser> getAssignees() {
      AtsUser unassigned = atsApi.getUserService().getUserById(AtsCoreUsers.UNASSIGNED_USER);
      return Arrays.asList(unassigned);
   }

   private final XModifiedListener listener = new XModifiedListener() {
      String fName = getLabel();

      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         createWorkflow(fName);
      }

   };

   protected void createWorkflow(String name) {
      String title = getTitle();
      for (IAtsTeamWorkflow sibling : atsApi.getWorkItemService().getSiblings(teamWf)) {
         if (sibling.getName().equals(title)) {
            WorkflowEditor.edit(sibling);
            return;
         }
      }

      if (creating) {
         AWorkbench.popup("Creating Workflow, Please Wait");
         return;
      }
      creating = true;

      String useName = name;
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), useName, useName + "?")) {
         creating = false;
         return;
      }

      final String fName = name;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            setLabel(fName + " - Creating...");
            comp.layout(true);
            parent.layout(true);
         }
      });

      final IAtsTeamWorkflow teamWf = this.teamWf;
      Job job = new Job(name) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            try {

               IAtsChangeSet changes = AtsApiService.get().createChangeSet(getName());
               Date createdDate = new Date();
               AtsUser currentUser = atsApi.getUserService().getCurrentUser();
               IAtsTeamDefinition teamDef = atsApi.getActionableItemService().getTeamDefinitionInherited(getAi());

               IAtsTeamWorkflow newTeamWf =
                  AtsApiService.get().getActionService().createTeamWorkflow(teamWf.getParentAction(), teamDef,
                     Collections.singleton(getAi()), getAssignees(), changes, createdDate, currentUser,
                     Collections.singleton(new NewEscapeActionListener()), CreateTeamOption.Duplicate_If_Exists);

               if (!changes.isEmpty()) {
                  changes.execute();
               }

               WorkflowEditor.edit(newTeamWf);

            } finally {
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     setLabel(fName);
                     creating = false;
                  }
               });
            }

            return Status.OK_STATUS;
         }
      };
      job.schedule();
   };

   public ArtifactTypeToken getOverrideCrArtifactType(IAtsTeamDefinition teamDef) {
      return AtsArtifactTypes.TeamWorkflow;
   }

   private class NewEscapeActionListener implements INewActionListener {
      @Override
      public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
         TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) teamWf.getStoreObject();
         teamArt.setName(getTitle());
      }

      @Override
      public AtsWorkDefinitionToken getOverrideWorkDefinitionId(IAtsTeamDefinition teamDef) {
         return AtsWorkDefinitionTokens.WorkDef_Team_Simple_InWork;
      }

      @Override
      public ArtifactTypeToken getOverrideArtifactType(IAtsTeamDefinition teamDef) {
         return getOverrideCrArtifactType(teamDef);
      }

   }

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return teamWf;
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.teamWf = (TeamWorkFlowArtifact) artifact;
      }
      super.setEditable(true);
   }

}
