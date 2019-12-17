/*
 * Created on Dec 17, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.workflow;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.core.workflow.AtsWorkItemServiceImpl;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkflowHookIde;

public class AtsWorkItemServiceClientImpl extends AtsWorkItemServiceImpl implements IAtsWorkItemServiceClient {

   private static Set<IAtsWorkflowHookIde> workflowHooksIde = new HashSet<>();

   @Override
   public void addWorkflowHookIde(IAtsWorkflowHookIde hook) {
      workflowHooksIde.add(hook);
   }

   public AtsWorkItemServiceClientImpl(AtsApi atsApi, ITeamWorkflowProvidersLazy teamWorkflowProvidersLazy) {
      super(atsApi, teamWorkflowProvidersLazy);
   }

   @Override
   public Set<IAtsWorkflowHookIde> getWorkflowHooksIde() {
      return workflowHooksIde;
   }

}
