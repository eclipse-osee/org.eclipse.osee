/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.dialog.AtsObjectNameSorter;
import org.eclipse.osee.ats.ide.workflow.WorkflowLabelProvider;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecoratorPreferences;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.cm.OseeCmEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class MultipleIdSearchOperation extends AbstractOperation implements IWorldEditorConsumer {
   private final Set<Artifact> resultAtsArts = new HashSet<>();
   private final Set<Artifact> resultNonAtsArts = new HashSet<>();
   private final Set<Artifact> artifacts = new HashSet<>();
   private final MultipleIdSearchData data;
   private boolean multiLine;

   public MultipleIdSearchOperation(MultipleIdSearchData data) {
      super(data.getName(), Activator.PLUGIN_ID);
      this.data = data;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!data.hasValidInput()) {
         MultipleIdSearchUi ui = new MultipleIdSearchUi(data);
         ui.setMultiLine(multiLine);
         if (!ui.getInput()) {
            return;
         }
      }
      if (data.getIds().isEmpty()) {
         AWorkbench.popup("Must Enter Valid Id");
         return;
      }
      searchAndSplitResults();
      if (resultAtsArts.isEmpty() && resultNonAtsArts.isEmpty()) {
         AWorkbench.popup("Invalid ID/Legacy PCR Id(s): " + Collections.toString(", ", data.getIds()));
         return;
      }
      if (resultNonAtsArts.size() > 0) {
         MassArtifactEditor.editArtifacts("Open Artifact(s)", new ArrayList<>(resultNonAtsArts));
      }
      if (resultAtsArts.size() > 0) {
         // If requested world editor and it's already been opened there,
         // don't process other arts in editors
         if (data.getWorldEditor() != null && data.getAtsEditor() == AtsEditor.WorldEditor) {
            data.getWorldEditor().getWorldComposite().load(getName(), resultAtsArts, TableLoadOption.None);
         } else {
            if (data.getAtsEditor() == AtsEditor.WorkflowEditor) {
               openWorkflowEditor(resultAtsArts);
            } else if (data.getAtsEditor() == AtsEditor.ChangeReport) {
               openChangeReport(resultAtsArts, data.getEnteredIds());
            } else if (data.getAtsEditor() == AtsEditor.ArtifactEditor) {
               for (Artifact artifact : resultAtsArts) {
                  AtsEditors.openArtifact(artifact, OseeCmEditor.ArtifactEditor);
               }
            } else {
               WorldEditor.open(new WorldEditorSimpleProvider(getName(), resultAtsArts));
            }
         }
      }
   }

   private void openChangeReport(Set<Artifact> artifacts, final String enteredIds) {
      final Set<Artifact> addedArts = new HashSet<>();
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(AtsArtifactTypes.Action)) {
            for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(artifact)) {
               if (AtsApiService.get().getBranchService().isCommittedBranchExists(
                  team) || AtsApiService.get().getBranchService().isWorkingBranchInWork(team)) {
                  addedArts.add(AtsApiService.get().getQueryServiceIde().getArtifact(team));
               }
            }
         }
         if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
            if (AtsApiService.get().getBranchService().isCommittedBranchExists(
               teamArt) || AtsApiService.get().getBranchService().isWorkingBranchInWork(teamArt)) {
               addedArts.add(artifact);
            }
         }
      }
      if (addedArts.size() == 1) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               for (Artifact art : addedArts) {
                  if (art.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                     AtsApiService.get().getBranchServiceIde().showChangeReport((TeamWorkFlowArtifact) art);
                  }
               }
            }
         });
      } else if (addedArts.size() > 0) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               ArtifactDecoratorPreferences artDecorator = new ArtifactDecoratorPreferences();
               artDecorator.setShowArtBranch(true);
               artDecorator.setShowArtType(true);
               artDecorator.setShowRelations(false);
               FilteredCheckboxTreeDialog<Artifact> dialog = new FilteredCheckboxTreeDialog<Artifact>(
                  "Select Available Change Reports", "Select available Change Reports to run.",
                  new ArrayTreeContentProvider(), new ArtifactLabelProvider(artDecorator), new AtsObjectNameSorter());
               dialog.setInput(addedArts);
               if (dialog.open() == Window.OK) {
                  if (dialog.getResult().length == 0) {
                     return;
                  }
                  for (Object obj : dialog.getResult()) {
                     AtsApiService.get().getBranchServiceIde().showChangeReport((TeamWorkFlowArtifact) obj);
                  }
               }
            }
         });
      } else {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openInformation(Displays.getActiveShell(), "Open Change Reports",
                  "No change report exists for " + enteredIds);
            }
         });
      }
   }

   private void openWorkflowEditor(final Set<Artifact> resultAtsArts) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            Artifact artifact = null;
            if (resultAtsArts.size() == 1) {
               artifact = resultAtsArts.iterator().next();
            } else {
               ListDialog ld = new ListDialog(Displays.getActiveShell());
               ld.setContentProvider(new ArrayContentProvider());
               ld.setLabelProvider(new WorkflowLabelProvider());
               ld.setTitle("Select Workflow");
               ld.setMessage("Select Workflow");
               ld.setInput(resultAtsArts);
               if (ld.open() == 0) {
                  artifact = AtsApiService.get().getQueryServiceIde().getArtifact(ld.getResult()[0]);
               } else {
                  return;
               }
            }
            if (artifact.isOfType(AtsArtifactTypes.Action)) {
               AtsEditors.openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               try {
                  WorkflowEditor.editArtifact(artifact);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }

   private void searchAndSplitResults() {

      Collection<TeamWorkFlowArtifact> teamArts =
         AtsApiService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andAttr(
            AtsAttributeTypes.LegacyPcrId, data.getIds()).getItems();

      resultAtsArts.addAll(teamArts);

      // This does artId search
      if (data.isIncludeArtIds() && data.getBranchForIncludeArtIds() != null) {
         List<ArtifactId> artifactIds = Collections.fromString(data.getEnteredIds(), ArtifactId::valueOf);
         for (Artifact art : ArtifactQuery.getArtifactListFrom(artifactIds, data.getBranchForIncludeArtIds())) {
            artifacts.add(art);
         }
      }

      // This does id search
      List<String> validIds = data.getIds();
      if (!validIds.isEmpty()) {
         for (Entry<String, IAtsWorkItem> entry : AtsApiService.get().getQueryService().getWorkItemsByAtsId(
            validIds).entrySet()) {
            artifacts.add((Artifact) entry.getValue().getStoreObject());
         }
      }

      for (Artifact art : artifacts) {
         if (art.isOfType(AtsArtifactTypes.AtsArtifact)) {
            resultAtsArts.add(art);
         } else {
            resultNonAtsArts.add(art);
         }
      }
   }

   @Override
   public void setWorldEditor(WorldEditor worldEditor) {
      data.setWorldEditor(worldEditor);
   }

   @Override
   public WorldEditor getWorldEditor() {
      return data.getWorldEditor();
   }

   @Override
   public String getName() {
      if (Strings.isValid(data.getEnteredIds())) {
         return String.format("%s - [%s]", super.getName(),
            Strings.truncate(data.getEnteredIds(), WorldEditor.TITLE_MAX_LENGTH));
      }
      return super.getName();
   }

   public void setMultiLine(boolean multiLine) {
      this.multiLine = multiLine;
   }

}
