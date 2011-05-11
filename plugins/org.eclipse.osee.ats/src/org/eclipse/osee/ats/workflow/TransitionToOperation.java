/*
 * Created on May 5, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public class TransitionToOperation extends AbstractOperation {

   private final Set<AbstractWorkflowArtifact> awas;
   private final String toStateName;

   public TransitionToOperation(String operationName, Set<AbstractWorkflowArtifact> awas, String toStateName) {
      super(operationName, AtsPlugin.PLUGIN_ID);
      this.awas = awas;
      this.toStateName = toStateName;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      XResultData rd = new XResultData(false);
      validateTransitions(rd, awas);
      if (rd.isErrors()) {
         rd.report(getName());
         return;
      }

      performTransition(awas);
   }

   private void performTransition(Set<AbstractWorkflowArtifact> awas) {
      XResultData rd = new XResultData(false);
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Transition-To");
         for (AbstractWorkflowArtifact awa : awas) {
            TransitionManager mgr = new TransitionManager(awa);
            StateDefinition stateDef = awa.getStateDefinitionByName(toStateName);
            Result result =
               mgr.transition(stateDef, awa.getStateMgr().getAssignees(), transaction, TransitionOption.Persist);
            if (result.isFalse()) {
               rd.logWithFormat("Transition failed for %s - [%s]", getArtifactName(awa), result.getText());
               return;
            }
         }
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         rd.logWithFormat("Exception [%s] checking transitioning to [%s].  See error log for details.",
            ex.getLocalizedMessage(), toStateName);
      } finally {
         for (AbstractWorkflowArtifact awa : awas) {
            try {
               awa.reloadAttributesAndRelations();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex, "Excption rolling back transition of $s",
                  getArtifactName(awa));
            }
         }
      }
      if (rd.isErrors()) {
         rd.report(getName());
      }
   }

   private void validateTransitions(XResultData rd, Set<AbstractWorkflowArtifact> awas) {
      for (AbstractWorkflowArtifact awa : awas) {
         StateDefinition toStateDef = null;
         for (StateDefinition stateDef : awa.getStateDefinition().getToStates()) {
            if (toStateName.equals(stateDef.getName())) {
               toStateDef = stateDef;
               break;
            }
         }
         if (toStateDef == null) {
            rd.logErrorWithFormat("Transition from [%s] to [%s] not valid for %s", awa.getCurrentStateName(),
               toStateName, getArtifactName(awa));
         } else {
            TransitionManager transitionManager = new TransitionManager(awa);
            try {
               Result result = transitionManager.isTransitionValid(toStateDef, null, TransitionOption.None);
               if (result.isFalse()) {
                  rd.logErrorWithFormat("Transition invalid for %s: %s", getArtifactName(awa), result.getText());
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               rd.logErrorWithFormat(
                  "Exception [%s] checking validity of transition for %s.  See error log for details.",
                  ex.getLocalizedMessage(), awa.toStringWithId());
            }
         }
      }
   }

   private String getArtifactName(AbstractWorkflowArtifact awa) {
      return String.format("[%s]%s", awa.getArtifactTypeName(), awa.toStringWithId());
   }
}
