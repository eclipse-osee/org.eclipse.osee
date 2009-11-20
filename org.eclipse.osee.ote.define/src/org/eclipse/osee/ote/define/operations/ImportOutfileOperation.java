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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.define.jobs.FindCommitableJob;
import org.eclipse.osee.ote.define.jobs.OutfileToArtifactJob;

/**
 * @author Roberto E. Escobar
 */
public class ImportOutfileOperation {

   private final URI[] fileSystemObjects;
   private final Branch selectedBranch;

   public ImportOutfileOperation(Branch selectedBranch, URI... fileSystemObjects) throws OseeCoreException {
      checkForNull(selectedBranch);
      checkForNull(fileSystemObjects);
      checkForEmpty(fileSystemObjects);

      this.fileSystemObjects = fileSystemObjects;
      this.selectedBranch = selectedBranch;
   }

   private void checkSuccessful(IStatus status) throws OseeStateException {
      if (status.equals(Status.OK_STATUS) != true && status.equals(Status.OK) != true) {
         throw new OseeStateException(String.format("Error detected: %s", status.getMessage()));
      }
   }

   private void checkForEmpty(Object[] items) throws OseeArgumentException {
      if (items.length <= 0) {
         throw new OseeArgumentException("No items to process. Size was 0.");
      }
   }

   private void checkForNull(Object object) throws OseeArgumentException {
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
         throw new IllegalArgumentException(String.format("Some items are not commitable. [%s]", toString(notAllowed)));
      }
      commitSelectedArtifacts(monitor, commitComment, job.getCommitAllowed());
   }

   private String toString(Artifact[] artifacts) {
      List<String> toReturn = new ArrayList<String>();
      for (Artifact artifact : artifacts) {
         toReturn.add(artifact.getName());
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(toReturn, ",\n");
   }

   private void commitSelectedArtifacts(IProgressMonitor monitor, String commitComment, Object[] items) throws Exception {
      Map<Branch, List<Artifact>> commitMap = getArtifactsByBranch(items);
      for (Branch branch : commitMap.keySet()) {
         monitor.setTaskName(String.format("Committing Artifacts into Branch: [%s]", branch.getName()));
         List<Artifact> artList = commitMap.get(branch);
         commitTestRunTx(monitor, commitComment, branch, artList.toArray(new Artifact[artList.size()]));
      }
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

   public static void commitTestRunTx(IProgressMonitor monitor, String commitComment, Branch branch, Artifact... artifacts) throws OseeCoreException {
      monitor.setTaskName("Persist Test Runs");
      for (Artifact artifact : artifacts) {
         monitor.subTask(String.format("Persisting: [%s] ", artifact.getName()));

         TestRunOperator operator = new TestRunOperator(artifact);
         operator.setLastDateUploaded(new Date());
         if (monitor.isCanceled() != false) {
            break;
         }
         monitor.worked(1);
      }

      Artifacts.persistInTransaction(commitComment, artifacts);
      if (monitor.isCanceled() != false) {
         throw new OseeCoreException("User Cancelled");
      }
      monitor.done();
   }
}
