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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.operations.ImportOutfileOperation;
import org.eclipse.osee.ote.ui.define.Activator;

/**
 * @author Roberto E. Escobar
 */
public class CommitTestRunJob extends Job {

   private static final String JOB_NAME = "Commit Test Runs";

   private final CommitJobDialog jobDialog;
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

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      jobDialog.schedule();
      try {
         jobDialog.join();
      } catch (InterruptedException ex1) {
         OseeLog.log(Activator.class, Level.SEVERE, ex1.toString(), ex1);
      }

      toReturn = jobDialog.getResult();
      if (toReturn.getSeverity() == IStatus.OK) {
         Object[] items = jobDialog.getSelected();
         String comment = jobDialog.getMessage();
         monitor.beginTask("Commit Artifacts", items.length * 2);
         try {
            committed = commitSelectedArtifacts(monitor, comment, items);
            toReturn = Status.OK_STATUS;
         } catch (Exception ex) {
            if (monitor.isCanceled() != true) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error committing Artifacts.", ex);
               toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error committing Artifacts.", ex);
            }
         }
      }
      return toReturn;
   }

   private Artifact[] commitSelectedArtifacts(IProgressMonitor monitor, String comment, Object[] items) throws Exception {
      Map<IOseeBranch, List<Artifact>> commitMap = getArtifactsByBranch(items);
      List<Artifact> committedList = new ArrayList<>();
      for (IOseeBranch branch : commitMap.keySet()) {
         monitor.setTaskName(String.format("Committing Artifacts into Branch: [%s]", branch.getName()));
         List<Artifact> artList = commitMap.get(branch);
         ImportOutfileOperation.commitTestRunTx(monitor, comment, branch,
            artList.toArray(new Artifact[artList.size()]));
         committedList.addAll(artList);
      }
      return committedList.toArray(new Artifact[committedList.size()]);
   }

   private Map<IOseeBranch, List<Artifact>> getArtifactsByBranch(Object[] items) {
      Map<IOseeBranch, List<Artifact>> branchMap = new HashMap<>();
      for (Object object : items) {
         Artifact testRun = (Artifact) object;
         IOseeBranch branch = testRun.getBranchToken();
         List<Artifact> artList = branchMap.get(branch);
         if (artList == null) {
            artList = new ArrayList<>();
            branchMap.put(branch, artList);
         }
         artList.add(testRun);
      }
      return branchMap;
   }
}