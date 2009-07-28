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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;

/**
 * @author Roberto E. Escobar
 */
public class LinkTestRunToTestScriptOperation {
   private static final String OPERATION_NAME = "Link Test Run to Test Script";
   private Artifact[] artifacts;
   private List<Artifact> unlinked;
   private List<Artifact> linked;

   public LinkTestRunToTestScriptOperation(Artifact... artifacts) {
      this.artifacts = artifacts;
      this.unlinked = new ArrayList<Artifact>();
      this.linked = new ArrayList<Artifact>();
   }

   public void execute(IProgressMonitor monitor) throws OseeArgumentException {
      int count = 0;
      monitor.setTaskName(OPERATION_NAME);
      for (Artifact testRun : artifacts) {

         monitor.subTask(String.format("Linking [%s] [%s of %s] ", testRun.getName(), ++count,
               artifacts.length));
         TestRunOperator operator = new TestRunOperator(testRun);
         try {
            operator.createTestScriptSoftLink();
            linked.add(testRun);
         } catch (Exception ex) {
            unlinked.add(testRun);
         }
         if (monitor.isCanceled() == true) {
            break;
         }
         monitor.worked(1);
      }
   }

   public Artifact[] getLinkedArtifacts() {
      return linked.toArray(new Artifact[linked.size()]);
   }

   public Artifact[] getUnlinkedArtifacts() {
      return unlinked.toArray(new Artifact[unlinked.size()]);
   }
}
