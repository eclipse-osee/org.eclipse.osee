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
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch.SearchOperator;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ArtifactImpactToActionSearchItem extends XNavigateItemAction {

   private static String TITLE = "Search Artifact Impact to Action";

   /**
    * @param parent
    */
   public ArtifactImpactToActionSearchItem(XNavigateItem parent) {
      super(parent, TITLE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      EntryDialog ed =
            new EntryDialog(
                  getName(),
                  "Searching on current default branch \"" + BranchPersistenceManager.getInstance().getDefaultBranch().getBranchName() + "\"\n\nEnter Artifact Name (or string) to search");
      if (ed.open() == 0) {
         ActionToArtifactImpactJob job = new ActionToArtifactImpactJob(ed.getEntry());
         job.setUser(true);
         job.setPriority(Job.LONG);
         job.schedule();
      }
   }

   public class ActionToArtifactImpactJob extends Job {
      private IProgressMonitor monitor;
      private final String artifactName;
      private XResultData rd = new XResultData(AtsPlugin.getLogger());

      public ActionToArtifactImpactJob(String artifactName) {
         super("Searching \"" + artifactName + "\"...");
         this.artifactName = artifactName;
      }

      public IStatus run(IProgressMonitor monitor) {
         this.monitor = monitor;
         try {
            getMatrixItems();
            rd.report(TITLE + " - \"" + artifactName + "\"");
            return Status.OK_STATUS;
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         }
      }

      private void getMatrixItems() throws SQLException {
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(null, artifactName,
                     BranchPersistenceManager.getInstance().getDefaultBranch(), SearchOperator.LIKE);
         Collection<Artifact> srchArts = srch.getArtifacts(Artifact.class);
         if (srchArts.size() == 0) return;
         int x = 1;
         rd.log("Searching for \"" + artifactName + "\"on current default branch \"" + BranchPersistenceManager.getInstance().getDefaultBranch().getBranchName() + "\"");
         rd.log("Found " + srchArts.size() + " matching artifacts.");
         for (Artifact srchArt : srchArts) {
            String str = String.format("Processing %d/%d - %s ", x++, srchArts.size(), srchArt.getDescriptiveName());
            System.out.println(str);
            rd.log("\n" + AHTML.bold(srchArt.getDescriptiveName()));
            monitor.subTask(str);
            Collection<TransactionData> transactions =
                  RevisionManager.getInstance().getTransactionsPerArtifact(srchArt, true);
            int y = 1;
            boolean found = false;
            StringBuffer sb = new StringBuffer();
            sb.append(AHTML.beginMultiColumnTable(95, 1));
            sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "HRID", "Title"}));
            for (TransactionData transData : transactions) {
               String transStr = String.format("Tranaction %d/%d", y++, transactions.size());
               System.out.println(transStr);
               monitor.subTask(transStr);
               if (transData.getCommitArtId() > 0) {
                  Artifact assocArt =
                        ArtifactPersistenceManager.getInstance().getArtifactFromId(transData.getCommitArtId(),
                              BranchPersistenceManager.getInstance().getAtsBranch());
                  if (assocArt instanceof TeamWorkFlowArtifact) {
                     sb.append(AHTML.addRowMultiColumnTable(new String[] {assocArt.getArtifactTypeName(),
                           assocArt.getHumanReadableId(), assocArt.getDescriptiveName()}));
                     found = true;
                  }
               }
            }
            sb.append(AHTML.endMultiColumnTable());
            if (found)
               rd.addRaw(sb.toString().replaceAll("\n", ""));
            else
               rd.log("  No Action Changes Found");
         }
      }
   }
}
