/*
 * Created on Mar 1, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.workflow;

import java.util.Date;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsWorkData;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public class AtsWorkData implements IAtsWorkData {

   private final AbstractWorkflowArtifact awa;

   public AtsWorkData(AbstractWorkflowArtifact awa) {
      this.awa = awa;
   }

   @Override
   public boolean isCompleted() throws OseeCoreException {
      return awa.isCompleted();
   }

   @Override
   public IAtsUser getCompletedBy() throws OseeCoreException {
      return awa.getCompletedBy();
   }

   @Override
   public Date getCompletedDate() throws OseeCoreException {
      return awa.getCompletedDate();
   }

   @Override
   public boolean isCancelled() throws OseeCoreException {
      return awa.isCancelled();
   }

   @Override
   public IAtsUser getCancelledBy() throws OseeCoreException {
      return awa.getCancelledBy();
   }

   @Override
   public Date getCancelledDate() throws OseeCoreException {
      return awa.getCancelledDate();
   }

   @Override
   public boolean isCompletedOrCancelled() throws OseeCoreException {
      return awa.isCompleted() || awa.isCancelled();
   }

   @Override
   public boolean isInWork() throws OseeCoreException {
      return awa.isInWork();
   }

   @Override
   public String getCompletedFromState() throws OseeCoreException {
      return awa.getCompletedFromState();
   }

   @Override
   public String getCancelledFromState() throws OseeCoreException {
      return awa.getCancelledFromState();
   }

   @Override
   public void setCompletedFromState(String state) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CompletedFromState, state);
   }

   @Override
   public void setCancelledFromState(String state) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CancelledFromState, state);
   }

   @Override
   public void setWorkPageType(WorkPageType workPageType) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, workPageType.name());
   }

   @Override
   public void setCompletedDate(Date completedDate) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, completedDate);
   }

   @Override
   public void setCancelledDate(Date cancelledDate) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, cancelledDate);
   }

   @Override
   public void setCompletedBy(IAtsUser completedBy) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, completedBy);
   }

   @Override
   public void setCancelledBy(IAtsUser cancelledBy) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CompletedDate, cancelledBy);
   }

}
