/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.mock;

import java.util.Date;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsWorkData;
import org.eclipse.osee.ats.core.workflow.WorkPageType;

public class MockWorkData implements IAtsWorkData {

   private WorkPageType workPageType;

   private Date completedDate, cancelledDate;
   private IAtsUser completedBy, cancelledBy;
   private String completedFromState, cancelledFromState, cancelledReason;

   public MockWorkData(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

   @Override
   public void setWorkPageType(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

   public String getCancelledReason() {
      return cancelledReason;
   }

   public void setCancelledReason(String cancelledReason) {
      this.cancelledReason = cancelledReason;
   }

   @Override
   public boolean isCompleted() {
      return workPageType.isCompletedPage();
   }

   @Override
   public IAtsUser getCompletedBy() {
      return completedBy;
   }

   @Override
   public boolean isCancelled() {
      return workPageType.isCancelledPage();
   }

   @Override
   public IAtsUser getCancelledBy() {
      return cancelledBy;
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isInWork() {
      return workPageType.isWorkingPage();
   }

   @Override
   public Date getCompletedDate() {
      return completedDate;
   }

   @Override
   public Date getCancelledDate() {
      return cancelledDate;
   }

   public WorkPageType getWorkPageType() {
      return workPageType;
   }

   public void setCompletedDate(Date completedDate) {
      this.completedDate = completedDate;
   }

   public void setCancelledDate(Date cancelledDate) {
      this.cancelledDate = cancelledDate;
   }

   public void setCompletedBy(IAtsUser completedBy) {
      this.completedBy = completedBy;
   }

   public void setCancelledBy(IAtsUser cancelledBy) {
      this.cancelledBy = cancelledBy;
   }

   @Override
   public String getCompletedFromState() {
      return completedFromState;
   }

   @Override
   public String getCancelledFromState() {
      return cancelledFromState;
   }

   @Override
   public void setCompletedFromState(String completedFromState) {
      this.completedFromState = completedFromState;
   }

   @Override
   public void setCancelledFromState(String cancelledFromState) {
      this.cancelledFromState = cancelledFromState;
   }

   public void setWorkingPageType(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

}
