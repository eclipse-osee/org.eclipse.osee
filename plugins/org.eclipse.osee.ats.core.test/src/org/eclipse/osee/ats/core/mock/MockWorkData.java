/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.mock;

import java.util.Date;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class MockWorkData implements IAtsWorkData {

   private StateType StateType;

   private Date completedDate, cancelledDate;
   private IAtsUser completedBy, cancelledBy;
   private String completedFromState, cancelledFromState, cancelledReason;

   public MockWorkData(StateType StateType) {
      this.StateType = StateType;
   }

   @Override
   public void setStateType(StateType StateType) {
      this.StateType = StateType;
   }

   public String getCancelledReason() {
      return cancelledReason;
   }

   public void setCancelledReason(String cancelledReason) {
      this.cancelledReason = cancelledReason;
   }

   @Override
   public boolean isCompleted() {
      return StateType.isCompletedState();
   }

   @Override
   public IAtsUser getCompletedBy() {
      return completedBy;
   }

   @Override
   public boolean isCancelled() {
      return StateType.isCancelledState();
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
      return StateType.isWorkingState();
   }

   @Override
   public Date getCompletedDate() {
      return completedDate;
   }

   @Override
   public Date getCancelledDate() {
      return cancelledDate;
   }

   public StateType getStateType() {
      return StateType;
   }

   @Override
   public void setCompletedDate(Date completedDate) {
      this.completedDate = completedDate;
   }

   @Override
   public void setCancelledDate(Date cancelledDate) {
      this.cancelledDate = cancelledDate;
   }

   @Override
   public void setCompletedBy(IAtsUser completedBy) {
      this.completedBy = completedBy;
   }

   @Override
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

   public void setWorkingPageType(StateType StateType) {
      this.StateType = StateType;
   }

   @Override
   public IAtsUser getCreatedBy() throws OseeCoreException {
      return null;
   }

   @Override
   public Date getCreatedDate() throws OseeCoreException {
      return null;
   }

   @Override
   public String getArtifactTypeName() throws OseeCoreException {
      return null;
   }
}
