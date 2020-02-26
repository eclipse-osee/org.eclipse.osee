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
package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractAtsArtifact;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.ReviewManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class AtsDeleteManager {

   public enum DeleteOption {
      Prompt,
      Delete,
      Purge
   };

   public static void handleDeletePurgeAtsObject(Collection<Artifact> selectedArts, boolean forcePend, DeleteOption... deleteOption) {
      final Collection<DeleteOption> deleteOptions = new ArrayList<>(Arrays.asList(deleteOption));
      boolean purgeOption = deleteOptions.contains(DeleteOption.Purge);
      List<Artifact> delArts = new ArrayList<>();
      StringBuilder artBuilder = new StringBuilder();

      for (Artifact art : selectedArts) {
         if (art instanceof AbstractAtsArtifact) {
            delArts.add(art);
            if (selectedArts.size() < 30) {
               artBuilder.append(String.format("Name: %s  Type: %s\n", ((AbstractAtsArtifact) art).getName(),
                  art.getArtifactTypeName()));
            }
         }
      }
      if (selectedArts.size() >= 5) {
         artBuilder.append(" < " + selectedArts.size() + " artifacts>");
      }
      boolean confirmDelete = true;
      // Prompt for delete if specified in options
      if (deleteOptions.contains(DeleteOption.Prompt)) {
         MessageDialogWithToggle md =
            MessageDialogWithToggle.openOkCancelConfirm(Displays.getActiveShell(), "Delete/Purge ATS Object",
               "Prepare to Delete/Purge ATS Object\n\n" + artBuilder.toString().replaceFirst("\n$",
                  "") + "\n\nAnd ALL its ATS children.\n(Artifacts will be retrieved for confirmation)\nAre You Sure?",
               "Purge", false, null, null);
         confirmDelete = md.getReturnCode() == 0;
         if (md.getToggleState()) {
            purgeOption = true;
         }
      }

      if (!confirmDelete) {
         return;
      }

      // Build list of related artifacts that will be deleted
      StringBuilder delBuilder = new StringBuilder();
      final Set<Artifact> allDeleteArts = new HashSet<>(30);
      Map<Artifact, Object> ignoredArts = new HashMap<>();
      getDeleteArtifacts(delArts, delBuilder, allDeleteArts, ignoredArts);
      // Need to have 'final' purge for use in the doWork below
      final boolean purge = purgeOption;
      // Get final confirmation of all selected and related items to delete/purge
      if (deleteOptions.contains(DeleteOption.Prompt)) {
         String results =
            (purge ? "Purge" : "Delete") + " ATS objects and related children, Are You Sure?\n" + delBuilder.toString();
         results = results.replaceAll("\n", "<br/>");
         HtmlDialog dialog = new HtmlDialog((purge ? "Purge" : "Delete") + " ATS objects and related children", "",
            AHTML.simplePage(results));
         dialog.open();
         if (dialog.getReturnCode() != 0) {
            return;
         }
      }
      AbstractOperation operation =
         new AbstractOperation((purge ? "Purge" : "Delete") + " ATS Objects", Activator.PLUGIN_ID) {

            @Override
            protected void doWork(IProgressMonitor monitor) throws Exception {
               // perform the delete/purge
               if (purge) {
                  Operations.executeWorkAndCheckStatus(new PurgeArtifacts(allDeleteArts));
               } else if (!allDeleteArts.isEmpty()) {
                  SkynetTransaction transaction =
                     TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Delete ATS Objects");
                  ArtifactPersistenceManager.deleteArtifactCollection(transaction, false, allDeleteArts);
                  transaction.execute();
               }
               if (deleteOptions.contains(DeleteOption.Prompt)) {
                  AWorkbench.popup((purge ? "Purge" : "Delete") + " Completed",
                     (purge ? "Purge" : "Delete") + " Completed");
               }
            }
         };
      if (forcePend) {
         Operations.executeWork(operation);
      } else {
         Operations.executeAsJob(operation, true);
      }

   }

   private static void getDeleteArtifacts(List<Artifact> delArts, StringBuilder delBuilder, final Set<Artifact> allDeleteArts, Map<Artifact, Object> ignoredArts) {
      for (Artifact deleteArt : delArts) {
         allDeleteArts.add(deleteArt);
         final Set<Artifact> relatedArts = new HashSet<>(30);
         delBuilder.append(String.format("\n<b>Selected</b>:[%s][%s][%s]", deleteArt.getArtifactTypeName(),
            AtsClientService.get().getAtsId(deleteArt), deleteArt.getName()) + "\n");
         if (deleteArt.isOfType(AtsArtifactTypes.Action)) {
            for (IAtsTeamWorkflow art : AtsClientService.get().getWorkItemService().getTeams(deleteArt)) {
               atsDelete((AbstractWorkflowArtifact) art, relatedArts, ignoredArts);
            }
         } else if (deleteArt.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            WorkflowEditor.close(java.util.Collections.singleton((AbstractWorkflowArtifact) deleteArt), true);
            atsDelete((AbstractWorkflowArtifact) deleteArt, relatedArts, ignoredArts);
            for (Artifact loopArt : relatedArts) {
               if (loopArt.notEqual(deleteArt)) {
                  delBuilder.append(
                     String.format(AHTML.addSpace(4) + "<b>Related</b>:[%s][%s][%s]", loopArt.getArtifactTypeName(),
                        AtsClientService.get().getAtsId(loopArt), loopArt.getName()) + "\n");
               }
            }
         }
         // check that if all team workflows are deleted, delete action
         for (Artifact art : allDeleteArts) {
            if (art instanceof AbstractWorkflowArtifact) {
               Artifact actionArt = ((AbstractWorkflowArtifact) art).getParentActionArtifact();
               if (actionArt != null) {
                  if (!allDeleteArts.contains(actionArt)) {
                     Collection<ArtifactId> teamWfArts =
                        AtsObjects.getArtifacts(AtsClientService.get().getWorkItemService().getTeams(actionArt));
                     if (allDeleteArts.containsAll(teamWfArts)) {
                        relatedArts.add(actionArt);
                        delBuilder.append(String.format(AHTML.addSpace(4) + "<b>Related</b>:[%s][%s][%s]",
                           actionArt.getArtifactTypeName(), AtsClientService.get().getAtsId(actionArt),
                           actionArt.getName()) + "\n");
                     }
                  }
               }
            }
         }
         allDeleteArts.addAll(relatedArts);
      }
      for (Artifact art : allDeleteArts) {
         if (art instanceof AbstractWorkflowArtifact) {
            WorkflowEditor.close(java.util.Collections.singleton((AbstractWorkflowArtifact) art), true);
         }
      }
   }

   private static void atsDelete(AbstractWorkflowArtifact awa, Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) {
      deleteArts.add(awa);
      for (Artifact relative : getBSideArtifacts(awa)) {
         allRelated.put(relative, awa);
      }
      if (awa.isTeamWorkflow()) {
         for (AbstractReviewArtifact reviewArt : ReviewManager.getReviews((TeamWorkFlowArtifact) awa)) {
            atsDelete(reviewArt, deleteArts, allRelated);
         }
         for (IAtsTask task : AtsClientService.get().getTaskService().getTasks((TeamWorkFlowArtifact) awa)) {
            atsDelete((AbstractWorkflowArtifact) task.getStoreObject(), deleteArts, allRelated);
         }
      }
   }

   private static List<Artifact> getBSideArtifacts(AbstractWorkflowArtifact awa) {
      List<Artifact> sideBArtifacts = new ArrayList<>();
      List<RelationLink> relatives = awa.getRelationsAll(DeletionFlag.EXCLUDE_DELETED);
      for (RelationLink link : relatives) {
         Artifact sideB = link.getArtifactB();
         if (sideB.notEqual(awa)) {
            sideBArtifacts.add(sideB);
         }
      }

      return sideBArtifacts;
   }

}