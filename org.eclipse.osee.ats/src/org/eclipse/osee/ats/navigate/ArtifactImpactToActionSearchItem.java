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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactCheckTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialogWithBranchSelect;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class ArtifactImpactToActionSearchItem extends XNavigateItemAction {

   private static String TITLE = "Search Artifact Impact to Action";

   /**
    * @param parent
    */
   public ArtifactImpactToActionSearchItem(XNavigateItem parent) {
      super(parent, TITLE, FrameworkImage.FLASHLIGHT);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      EntryDialogWithBranchSelect ed =
            new EntryDialogWithBranchSelect(getName(), "Enter Artifact Name (or string) to search (no wildcards)");
      if (ed.open() == 0) {
         ActionToArtifactImpactJob job = new ActionToArtifactImpactJob(ed.getEntry(), ed.getBranch());
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
      }
   }

   public class ActionToArtifactImpactJob extends Job {
      private IProgressMonitor monitor;
      private final String artifactName;
      private final XResultData rd = new XResultData();
      private final Branch branch;

      public ActionToArtifactImpactJob(String artifactName, Branch branch) {
         super("Searching \"" + artifactName + "\"...");
         this.artifactName = artifactName;
         this.branch = branch;
      }

      @Override
      public IStatus run(IProgressMonitor monitor) {
         this.monitor = monitor;
         try {
            getMatrixItems();
            rd.report(TITLE + " - \"" + artifactName + "\"");
            return Status.OK_STATUS;
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
      }

      private void getMatrixItems() throws OseeCoreException {
         final Collection<Artifact> srchArts =
               ArtifactQuery.getArtifactsFromName("%" + artifactName + "%", branch, true);
         final Set<Artifact> processArts = new HashSet<Artifact>();
         if (srchArts.size() == 0) return;
         if (srchArts.size() > 1) {
            Displays.ensureInDisplayThread(new Runnable() {
               /* (non-Javadoc)
                * @see java.lang.Runnable#run()
                */
               @Override
               public void run() {
                  ArtifactCheckTreeDialog dialog = new ArtifactCheckTreeDialog(srchArts);
                  dialog.setTitle(TITLE);
                  dialog.setMessage("Select Artifacts to Search");
                  if (dialog.open() == 0) {
                     processArts.addAll(dialog.getSelection());
                  }
               }
            }, true);

         } else {
            processArts.addAll(srchArts);
         }
         int x = 1;
         rd.log("Artifact Impact to Action for artifact(s) on branch \"" + branch.getBranchShortName() + "\"");

         HashCollection<Artifact, TransactionId> transactionMap = ChangeManager.getModifingTransactions(processArts);
         HashCollection<Artifact, Branch> branchMap = ChangeManager.getModifingBranches(processArts);
         for (Artifact srchArt : processArts) {
            String str = String.format("Processing %d/%d - %s ", x++, processArts.size(), srchArt.getDescriptiveName());
            System.out.println(str);
            rd.log("\n" + AHTML.bold(srchArt.getDescriptiveName()));
            monitor.subTask(str);
            int y = 1;
            rd.addRaw(AHTML.beginMultiColumnTable(95, 1));
            rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Status", "HRID", "Title"}));

            // Check for changes on working branches
            boolean workingBranchesFound = false;

            Collection<Branch> branches = branchMap.getValues(srchArt);
            if (branches != null) {
               for (Branch branch : branches) {
                  Artifact assocArt = branch.getAssociatedArtifact();
                  if (assocArt != null && !assocArt.equals(UserManager.getUser(SystemUser.OseeSystem))) {
                     rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {assocArt.getArtifactTypeName(), "Working",
                           XResultData.getHyperlink(assocArt), assocArt.getDescriptiveName()}));
                  } else {
                     rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {"Branch", "", branch.getBranchName()}));
                  }
                  workingBranchesFound = true;
               }
            }
            if (!workingBranchesFound) {
               rd.addRaw(AHTML.addRowSpanMultiColumnTable("No Impacting Working Branches Found", 3));
            }
            // Add committed changes
            boolean committedChanges = false;
            Collection<TransactionId> transactions = transactionMap.getValues(srchArt);
            if (transactions != null) {
               for (TransactionId transactionId : transactions) {
                  String transStr = String.format("Tranaction %d/%d", y++, transactions.size());
                  System.out.println(transStr);
                  monitor.subTask(transStr);
                  if (transactionId.getCommitArtId() > 0) {
                     Artifact assocArt =
                           ArtifactQuery.getArtifactFromId(transactionId.getCommitArtId(),
                                 BranchManager.getCommonBranch());
                     if (assocArt instanceof TeamWorkFlowArtifact) {
                        rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {assocArt.getArtifactTypeName(),
                              "Committed", assocArt.getHumanReadableId(), assocArt.getDescriptiveName()}));
                        committedChanges = true;
                     }
                  }
               }
            }
            if (!committedChanges) {
               rd.addRaw(AHTML.addRowSpanMultiColumnTable("No Impacting Actions Found", 3));
            }
            rd.addRaw(AHTML.endMultiColumnTable());
         }
      }
   }
}
