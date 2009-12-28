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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class AtsDeleteManager {

   public enum DeleteOption {
      Prompt, Delete, Purge
   };

   public static void handleDeletePurgeAtsObject(Collection<? extends Artifact> selectedArts, boolean forcePend, DeleteOption... deleteOption) throws OseeCoreException {
      final Collection<DeleteOption> deleteOptions = Collections.getAggregate(deleteOption);
      ArrayList<Artifact> delArts = new ArrayList<Artifact>();
      StringBuilder artBuilder = new StringBuilder();

      for (Artifact art : selectedArts) {
         if (art instanceof ATSArtifact) {
            delArts.add(art);
            if (selectedArts.size() < 30) {
               artBuilder.append(String.format("Name: %s  Type: %s\n", art.getHumanReadableId(),
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
               MessageDialogWithToggle.openOkCancelConfirm(
                     Display.getCurrent().getActiveShell(),
                     "Delete/Purge ATS Object",
                     "Prepare to Delete/Purge ATS Object\n\n" + artBuilder.toString().replaceFirst("\n$", "") + "\n\nAnd ALL its ATS children.\n(Artifacts will be retrieved for confirmation)\nAre You Sure?",
                     "Purge", false, null, null);
         confirmDelete = md.getReturnCode() == 0;
         if (md.getToggleState()) {
            deleteOptions.add(DeleteOption.Purge);
         }
      }
      if (!confirmDelete) {
         return;
      }
      // Build list of related artifacts that will be deleted
      StringBuilder delBuilder = new StringBuilder();
      final Set<Artifact> allDeleteArts = new HashSet<Artifact>(30);
      Map<Artifact, Object> ignoredArts = new HashMap<Artifact, Object>();
      for (Artifact deleteArt : delArts) {
         allDeleteArts.add(deleteArt);
         final Set<Artifact> relatedArts = new HashSet<Artifact>(30);
         delBuilder.append(String.format("\n<b>Selected</b>:[%s][%s][%s]", deleteArt.getArtifactTypeName(),
               deleteArt.getHumanReadableId(), deleteArt.getName()) + "\n");
         ((ATSArtifact) deleteArt).atsDelete(relatedArts, ignoredArts);
         for (Artifact loopArt : relatedArts) {
            if (!loopArt.equals(deleteArt)) {
               delBuilder.append(String.format(AHTML.addSpace(4) + "<b>Related</b>:[%s][%s][%s]",
                     loopArt.getArtifactTypeName(), loopArt.getHumanReadableId(), loopArt.getName()) + "\n");
            }
         }
         // check that if all team workflows are deleted, delete action
         for (Artifact art : allDeleteArts) {
            if (art instanceof StateMachineArtifact) {
               ActionArtifact actionArt = ((StateMachineArtifact) art).getParentActionArtifact();
               if (actionArt != null && !allDeleteArts.contains(actionArt) && allDeleteArts.containsAll(actionArt.getTeamWorkFlowArtifacts())) {
                  relatedArts.add(actionArt);
                  delBuilder.append(String.format(AHTML.addSpace(4) + "<b>Related</b>:[%s][%s][%s]",
                        actionArt.getArtifactTypeName(), actionArt.getHumanReadableId(), actionArt.getName()) + "\n");
               }
            }
         }
         allDeleteArts.addAll(relatedArts);
      }
      final boolean purge = deleteOptions.contains(DeleteOption.Purge);
      // Get final confirmation of all seleted and related items to delete/purge
      if (deleteOptions.contains(DeleteOption.Prompt)) {
         String results =
               (purge ? "Purge" : "Delete") + " ATS objects and related children, Are You Sure?\n" + delBuilder.toString();
         results = results.replaceAll("\n", "<br>");
         HtmlDialog dialog =
               new HtmlDialog((purge ? "Purge" : "Delete") + " ATS objects and related children", "",
                     AHTML.simplePage(results));
         dialog.open();
         if (dialog.getReturnCode() != 0) {
            return;
         }
      }
      AbstractOperation operation =
            new AbstractOperation((purge ? "Purge" : "Delete") + " ATS Objects", AtsPlugin.PLUGIN_ID) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  // perform the delete/purge
                  if (purge) {
                     purgeArtifacts(allDeleteArts);
                  } else {
                     SkynetTransaction transaction =
                           new SkynetTransaction(AtsUtil.getAtsBranch(), "Delete ATS Objects");
                     ArtifactPersistenceManager.deleteArtifact(transaction, false,
                           allDeleteArts.toArray(new Artifact[allDeleteArts.size()]));
                     transaction.execute();
                  }
                  if (deleteOptions.contains(DeleteOption.Prompt)) {
                     AWorkbench.popup((purge ? "Purge" : "Delete") + " Completed",
                           (purge ? "Purge" : "Delete") + " Completed");
                  }
               }
            };
      if (forcePend) {
         Operations.executeAndPend(operation, true);
      } else {
         Operations.executeAsJob(operation, true);
      }

   }

   private static void purgeArtifacts(Collection<Artifact> artifacts) throws OseeDataStoreException, OseeCoreException {
      for (Artifact art : artifacts) {
         art.purgeFromBranch();
      }
   }
}
