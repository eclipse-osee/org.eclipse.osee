/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.SmaWorkflowLabelProvider;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.LegacyPCRActions;
import org.eclipse.osee.ats.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorOperationProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecoratorPreferences;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactViewerSorter;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class MultipleHridSearchOperation extends AbstractOperation implements IWorldEditorConsumer {
   private final Set<Artifact> resultAtsArts = new HashSet<Artifact>();
   private final Set<Artifact> resultNonAtsArts = new HashSet<Artifact>();
   private final Set<Artifact> artifacts = new HashSet<Artifact>();
   private final MultipleHridSearchData data;

   public MultipleHridSearchOperation(MultipleHridSearchData data) {
      super(data.getName(), AtsPlugin.PLUGIN_ID);
      this.data = data;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!data.hasValidInput()) {
         MultipleHridSearchUi ui = new MultipleHridSearchUi(data);
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
         AWorkbench.popup("Invalid HRID/Guid/Legacy PCR Id(s): " + Collections.toString(data.getIds(), ", "));
         return;
      }
      if (resultNonAtsArts.size() > 0) {
         RendererManager.openInJob(new ArrayList<Artifact>(resultNonAtsArts), PresentationType.GENERALIZED_EDIT);
      }
      if (resultAtsArts.size() > 0) {
         // If requested world editor and it's already been opened there, don't process other arts in editors
         if (data.getWorldEditor() != null && data.getAtsEditor() == AtsEditor.WorldEditor) {
            data.getWorldEditor().getWorldComposite().load(getName(), resultAtsArts, TableLoadOption.None);
         } else {
            if (data.getAtsEditor() == AtsEditor.WorkflowEditor) {
               openWorkflowEditor(resultAtsArts);
            } else if (data.getAtsEditor() == AtsEditor.ChangeReport) {
               openChangeReport(resultAtsArts, data.getEnteredIds());
            } else {
               WorldEditor.open(new WorldEditorOperationProvider(this));
            }
         }
      }
   }

   private void openChangeReport(Set<Artifact> artifacts, final String enteredIds) {
      try {
         final Set<Artifact> addedArts = new HashSet<Artifact>();
         for (Artifact artifact : artifacts) {
            if (artifact instanceof ActionArtifact) {
               for (TeamWorkFlowArtifact team : ((ActionArtifact) artifact).getTeamWorkFlowArtifacts()) {
                  if (team.getBranchMgr().isCommittedBranchExists() || team.getBranchMgr().isWorkingBranchInWork()) {
                     addedArts.add(team);
                  }
               }
            }
            if (artifact instanceof TeamWorkFlowArtifact) {
               if (((TeamWorkFlowArtifact) artifact).getBranchMgr().isCommittedBranchExists() || ((TeamWorkFlowArtifact) artifact).getBranchMgr().isWorkingBranchInWork()) {
                  addedArts.add(artifact);
               }
            }
         }
         if (addedArts.size() == 1) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  for (Artifact art : addedArts) {
                     if (art instanceof TeamWorkFlowArtifact) {
                        ((TeamWorkFlowArtifact) art).getBranchMgr().showChangeReport();
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
                  SimpleCheckFilteredTreeDialog dialog =
                     new SimpleCheckFilteredTreeDialog("Select Available Change Reports",
                        "Select available Change Reports to run.", new ArrayTreeContentProvider(),
                        new ArtifactLabelProvider(artDecorator), new ArtifactViewerSorter(), 0, Integer.MAX_VALUE);
                  dialog.setInput(addedArts);
                  if (dialog.open() == 0) {
                     if (dialog.getResult().length == 0) {
                        return;
                     }
                     for (Object obj : dialog.getResult()) {
                        ((TeamWorkFlowArtifact) obj).getBranchMgr().showChangeReport();
                     }
                  }
               }
            });
         } else {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Open Change Reports",
                     "No change report exists for " + enteredIds);
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
               ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
               ld.setContentProvider(new ArrayContentProvider());
               ld.setLabelProvider(new SmaWorkflowLabelProvider());
               ld.setTitle("Select Workflow");
               ld.setMessage("Select Workflow");
               ld.setInput(resultAtsArts);
               if (ld.open() == 0) {
                  artifact = (Artifact) ld.getResult()[0];
               }
            }
            if (artifact instanceof ActionArtifact) {
               AtsUtil.openATSAction(artifact, AtsOpenOption.OpenOneOrPopupSelect);
            } else {
               try {
                  SMAEditor.editArtifact(artifact);
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }

   private void searchAndSplitResults() throws OseeCoreException {
      resultAtsArts.addAll(LegacyPCRActions.getTeamsTeamWorkflowArtifacts(data.getIds(),
         (Collection<TeamDefinitionArtifact>) null));

      // This does artId search
      if (data.isIncludeArtIds() && data.getBranchForIncludeArtIds() != null) {
         for (Artifact art : ArtifactQuery.getArtifactListFromIds(Lib.stringToIntegerList(data.getEnteredIds()),
            data.getBranchForIncludeArtIds())) {
            artifacts.add(art);
         }
      }
      // This does hrid/guid search
      for (Artifact art : ArtifactQuery.getArtifactListFromIds(data.getIds(), AtsUtil.getAtsBranch())) {
         artifacts.add(art);
      }

      for (Artifact art : artifacts) {
         if (art instanceof IATSArtifact) {
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
         return String.format("%s - [%s]", super.getName(), data.getEnteredIds());
      }
      return super.getName();
   }

}
