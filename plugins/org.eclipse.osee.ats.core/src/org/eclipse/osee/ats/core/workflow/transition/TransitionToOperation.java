/*
 * Created on May 5, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

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
         SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), helper.getName());
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
