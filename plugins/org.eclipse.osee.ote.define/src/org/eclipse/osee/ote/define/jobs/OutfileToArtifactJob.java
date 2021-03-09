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

package org.eclipse.osee.ote.define.jobs;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.internal.Activator;
import org.eclipse.osee.ote.define.operations.LinkTestRunToTestScriptOperation;
import org.eclipse.osee.ote.define.operations.OutfileToArtifactOperation;

/**
 * @author Roberto E. Escobar
 */
public class OutfileToArtifactJob extends Job {

   private static final String JOB_NAME = "Convert Outfile to Artifact";
   private final URI[] filesToImport;
   private Artifact[] results;
   private URI[] filesWithErrors;
   private final BranchToken branch;

   public OutfileToArtifactJob(BranchToken branch, URI... filesToImport) {
      super(JOB_NAME);
      setUser(true);
      setPriority(Job.LONG);
      this.branch = branch;
      this.filesToImport = filesToImport;
      this.results = new Artifact[0];
      this.filesWithErrors = new URI[0];
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      IStatus toReturn = Status.OK_STATUS;
      try {
         monitor.beginTask(getName(), filesToImport.length * 2);
         OutfileToArtifactOperation outfileToArtifactOperation = new OutfileToArtifactOperation(branch, filesToImport);
         outfileToArtifactOperation.execute(monitor);
         results = outfileToArtifactOperation.getResults();
         filesWithErrors = outfileToArtifactOperation.getUnparseableFiles();
         if (monitor.isCanceled() != true) {
            LinkTestRunToTestScriptOperation linkingOperation = new LinkTestRunToTestScriptOperation(results);
            linkingOperation.execute(monitor);
         }

      } catch (Exception ex) {
         toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.subTask("Done");
         monitor.done();
      }
      return toReturn;
   }

   public Artifact[] getResults() {
      return results;
   }

   public URI[] getUnparseableFiles() {
      return filesWithErrors;
   }
}
