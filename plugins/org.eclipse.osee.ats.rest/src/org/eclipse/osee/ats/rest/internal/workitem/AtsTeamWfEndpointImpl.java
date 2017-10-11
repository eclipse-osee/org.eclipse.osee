package org.eclipse.osee.ats.rest.internal.workitem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.CompareResults;

/**
 * Donald G. Dunne
 */
@Path("teamwf")
public class AtsTeamWfEndpointImpl implements AtsTeamWfEndpointApi {

   private final AtsApi services;

   public AtsTeamWfEndpointImpl(AtsApi services) {
      this.services = services;
   }

   @Override
   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public CompareResults getChangeData(@PathParam("id") String id) {
      IAtsWorkItem workItem = services.getWorkItemService().getWorkItemByAnyId(id);
      if (!workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      TransactionToken trans = services.getBranchService().getEarliestTransactionId(teamWf);
      if (trans.isValid()) {
         return services.getBranchService().getChangeData(trans);
      }
      BranchId branch = services.getBranchService().getWorkingBranch(teamWf);
      if (branch.isValid()) {
         return services.getBranchService().getChangeData(branch);
      }
      return new CompareResults();
   }

}
