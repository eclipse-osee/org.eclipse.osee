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
package org.eclipse.osee.ats.core.client.workflow;

import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
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
   public void setStateType(StateType StateType) throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, StateType.name());
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

   @Override
   public IAtsUser getCreatedBy() throws OseeCoreException {
      return AtsClientService.get().getUserService().getUserById(
         awa.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, ""));
   }

   @Override
   public Date getCreatedDate() throws OseeCoreException {
      return awa.getSoleAttributeValue(AtsAttributeTypes.CreatedDate);
   }

   @Override
   public String getArtifactTypeName() throws OseeCoreException {
      return awa.getArtifactTypeName();
   }

}
