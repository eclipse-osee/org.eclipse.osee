/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.define.operations;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.define.jobs.FindCommitableJob;
import org.eclipse.osee.ote.define.jobs.OutfileToArtifactJob;

/**
 * @author Roberto E. Escobar
 */
public class ImportOutfileOperation {

   private final URI[] fileSystemObjects;
   private final BranchToken selectedBranch;

   public ImportOutfileOperation(BranchToken selectedBranch, URI... fileSystemObjects) {
      checkForNull(selectedBranch);
      checkForNull(fileSystemObjects);
      checkForEmpty(fileSystemObjects);

      this.fileSystemObjects = fileSystemObjects;
      this.selectedBranch = selectedBranch;
   }

   private void checkSuccessful(IStatus status) {
      if (status.equals(Status.OK_STATUS) != true && status.equals(IStatus.OK) != true) {
         throw new OseeStateException("Error detected: %s", status.getMessage());
      }
   }

   private void checkForEmpty(Object[] items) {
      if (items.length <= 0) {
         throw new OseeArgumentException("No items to process. Size was 0.");
      }
   }

   private void checkForNull(Object object) {
      if (object == null) {
         throw new OseeArgumentException("Object cannot be null");
      }
   }

   public void execute(IProgressMonitor monitor) throws Exception {
      OutfileToArtifactJob job = new OutfileToArtifactJob(selectedBranch, fileSystemObjects);
      job.setUser(false);
      job.schedule();
      job.join();

      checkSuccessful(job.getResult());

      // Report Parse Errors
      URI[] itemsWithError = job.getUnparseableFiles();
      if (itemsWithError.length > 0) {
         throw new Exception(String.format("Error parsing files [%s]", Arrays.deepToString(itemsWithError)));
      } else {
         launchFindCommitableJob(monitor, "", job.getResults());
      }
   }

   private void launchFindCommitableJob(IProgressMonitor monitor, String commitComment, final Artifact[] artifacts) throws Exception {
      // Find Commit Allowed
      FindCommitableJob job = new FindCommitableJob(artifacts);
      job.setUser(false);
      job.schedule();
      job.join();

      checkSuccessful(job.getResult());

      Artifact[] notAllowed = job.getCommitNotAllowed();
      if (notAllowed.length > 0) {
         throw new OseeArgumentException("Some items are not commitable. [%s]", toString(notAllowed));
      }
      commitSelectedArtifacts(monitor, commitComment, job.getCommitAllowed());
   }

   private String toString(Artifact[] artifacts) {
      return Collections.toString(Arrays.asList(artifacts), ",\n", Named::getName);
   }

   private void commitSelectedArtifacts(IProgressMonitor monitor, String commitComment, Object[] items) throws Exception {
      Map<BranchToken, List<Artifact>> commitMap = getArtifactsByBranch(items);
      for (BranchToken branch : commitMap.keySet()) {
         monitor.setTaskName(String.format("Committing Artifacts into Branch: [%s]", branch.getName()));
         List<Artifact> artList = commitMap.get(branch);
         commitTestRunTx(monitor, commitComment, branch, artList.toArray(new Artifact[artList.size()]));
      }
   }

   private Map<BranchToken, List<Artifact>> getArtifactsByBranch(Object[] items) {
      Map<BranchToken, List<Artifact>> branchMap = new HashMap<>();
      for (Object object : items) {
         Artifact testRun = (Artifact) object;
         BranchToken branch = testRun.getBranchToken();
         List<Artifact> artList = branchMap.get(branch);
         if (artList == null) {
            artList = new ArrayList<>();
            branchMap.put(branch, artList);
         }
         artList.add(testRun);
      }
      return branchMap;
   }

   public static void commitTestRunTx(IProgressMonitor monitor, String commitComment, BranchId branch, Artifact... artifacts) {
      monitor.setTaskName("Persist Test Runs");
      Date uploadDate = new Date();
      for (Artifact artifact : artifacts) {
         monitor.subTask(String.format("Persisting: [%s] ", artifact.getName()));

         ArtifactTestRunOperator operator = new ArtifactTestRunOperator(artifact);
         operator.setLastDateUploaded(uploadDate);
         if (monitor.isCanceled() != false) {
            break;
         }
         monitor.worked(1);
      }

      TransactionManager.persistInTransaction(commitComment, artifacts);
      if (monitor.isCanceled() != false) {
         throw new OseeCoreException("User Cancelled");
      }
      monitor.done();
   }
}
