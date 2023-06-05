/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow.transition;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResult;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TransitionToOperation extends AbstractOperation {

   private final TransitionData transData;
   private TransitionResults results;

   public TransitionToOperation(TransitionData transData) {
      super(transData.getName(), Activator.PLUGIN_ID);
      this.transData = transData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(transData.getName() + ".preSave");
         for (IAtsWorkItem workItem : transData.getWorkItems()) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) workItem;
            if (awa.isDirty()) {
               changes.add(awa);
            }
         }
         changes.executeIfNeeded();

         results = AtsApiService.get().getWorkItemServiceIde().transition(transData);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         if (results == null) {
            results = new TransitionResults();
         }
         results.addResult(
            new TransitionResult(String.format("Exception [%s] transitioning to [%s].  See error log for details.",
               ex.getLocalizedMessage(), transData.getToStateName())));
      }
   }

   public TransitionResults getResults() {
      return results;
   }

}
