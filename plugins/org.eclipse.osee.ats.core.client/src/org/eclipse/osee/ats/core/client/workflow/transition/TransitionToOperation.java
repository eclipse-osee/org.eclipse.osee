/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.workflow.transition;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResult;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class TransitionToOperation extends AbstractOperation {

   private final ITransitionHelper helper;
   private TransitionResults results;

   public TransitionToOperation(ITransitionHelper helper) {
      super(helper.getName(), Activator.PLUGIN_ID);
      this.helper = helper;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      try {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), helper.getName() + ".preSave");
         for (AbstractWorkflowArtifact awa : helper.getAwas()) {
            if (awa.isDirty()) {
               awa.persist(transaction);
            }
         }
         transaction.execute();

         transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), helper.getName());
         TransitionManager transitionMgr = new TransitionManager(helper, transaction);
         results = transitionMgr.handleAll();
         if (results.isCancelled()) {
            return;
         } else if (results.isEmpty()) {
            transaction.execute();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         results.addResult(new TransitionResult(String.format(
            "Exception [%s] transitioning to [%s].  See error log for details.", ex.getLocalizedMessage(),
            helper.getToStateName())));
      }
   }

   public TransitionResults getResults() {
      return results;
   }

}
