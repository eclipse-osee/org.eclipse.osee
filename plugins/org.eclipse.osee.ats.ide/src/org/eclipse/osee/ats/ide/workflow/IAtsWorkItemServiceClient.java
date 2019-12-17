/*
 * Created on Dec 17, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.workflow;

import java.util.Set;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkflowHookIde;

public interface IAtsWorkItemServiceClient extends IAtsWorkItemService {

   public Set<IAtsWorkflowHookIde> getWorkflowHooksIde();

   void addWorkflowHookIde(IAtsWorkflowHookIde hook);

}
