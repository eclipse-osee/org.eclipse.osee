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
package org.eclipse.osee.ote.ui.define.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.define.operations.ImportOutfileOperation;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;

/**
 * @author Roberto E. Escobar
 */
public class CommitTestRunJob extends Job {

   private static final String JOB_NAME = "Commit Test Runs";

   private CommitJobDialog jobDialog;
   private Artifact[] committed;

   public CommitTestRunJob(Artifact[] allitems, Artifact[] preSelected, boolean isOverrideAllowed) {
      this(allitems, preSelected, null, isOverrideAllowed);
   }

   public CommitTestRunJob(Artifact[] allitems, Artifact[] preSelected, Artifact[] unselectable, boolean isOverrideAllowed) {
      super(JOB_NAME);
      jobDialog = new CommitJobDialog(allitems, preSelected, unselectable, isOverrideAllowed);
   }

   public Artifact[] getCommitted() {
      return committed;
   }

   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      jobDialog.schedule();
      try {
         jobDialog.join();
      } catch (InterruptedException ex1) {
         OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, ex1.toString(), ex1);
      }

      toReturn = jobDialog.getResult();
      if (toReturn.getSeverity() == IStatus.OK) {
         Object[] items = jobDialog.getSelected();
         String comment = jobDialog.getMessage();
         monitor.beginTask("Commit Artifacts", items.length * 2);
         try {
            commitSelectedArtifacts(monitor, comment, items);
            toReturn = Status.OK_STATUS;
            committed = verifyItemsCommitted(monitor, items);
         } catch (Exception ex) {
            if (monitor.isCanceled() != true) {
               OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, "Error committing Artifacts.", ex);
               toReturn = new Status(Status.ERROR, OteUiDefinePlugin.PLUGIN_ID, "Error committing Artifacts.", ex);
            }
         }
      }
      return toReturn;
   }

   private void commitSelectedArtifacts(IProgressMonitor monitor, String comment, Object[] items) throws Exception {
      Map<Branch, List<Artifact>> commitMap = getArtifactsByBranch(items);
      for (Branch branch : commitMap.keySet()) {
         monitor.setTaskName(String.format("Committing Artifacts into Branch: [%s]", branch.getBranchName()));
         List<Artifact> artList = commitMap.get(branch);
         ImportOutfileOperation.commitTestRunTx(monitor, comment, branch, artList.toArray(new Artifact[artList.size()]));
      }
   }

   private Artifact[] verifyItemsCommitted(IProgressMonitor monitor, Object[] items) throws OseeArgumentException {
      monitor.setTaskName("Verify committed...");
      List<Artifact> committedList = new ArrayList<Artifact>();
      for (Object object : items) {
         Artifact artifact = (Artifact) object;
         if (new TestRunOperator(artifact).isCommitAllowed() != true) {
            committedList.add(artifact);
         }
         monitor.worked(1);
      }
      return committedList.toArray(new Artifact[committedList.size()]);
   }

   private Map<Branch, List<Artifact>> getArtifactsByBranch(Object[] items) {
      Map<Branch, List<Artifact>> branchMap = new HashMap<Branch, List<Artifact>>();
      for (Object object : items) {
         Artifact testRun = (Artifact) object;
         Branch branch = testRun.getBranch();
         List<Artifact> artList = branchMap.get(branch);
         if (artList == null) {
            artList = new ArrayList<Artifact>();
            branchMap.put(branch, artList);
         }
         artList.add(testRun);
      }
      return branchMap;
   }
}