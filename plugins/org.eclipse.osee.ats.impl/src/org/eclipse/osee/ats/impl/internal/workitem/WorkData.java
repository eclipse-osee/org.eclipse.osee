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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class WorkData implements IAtsWorkData {

   private final ArtifactReadable artifact;
   private final IAtsUserService atsUserService;

   public WorkData(IAtsUserService atsUserService, IAtsWorkItem workItem, ArtifactReadable artifact) {
      this.atsUserService = atsUserService;
      this.artifact = artifact;
   }

   @Override
   public boolean isCompleted() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType).equals(StateType.Completed.name());
   }

   @Override
   public IAtsUser getCompletedBy() throws OseeCoreException {
      return atsUserService.getUserById(artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedBy, ""));
   }

   @Override
   public Date getCompletedDate() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedDate);
   }

   @Override
   public boolean isCancelled() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType).equals(StateType.Cancelled.name());
   }

   @Override
   public IAtsUser getCancelledBy() throws OseeCoreException {
      return atsUserService.getUserById(artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledBy, ""));
   }

   @Override
   public Date getCancelledDate() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledDate);
   }

   @Override
   public boolean isCompletedOrCancelled() throws OseeCoreException {
      return isCompleted() || isCancelled();
   }

   @Override
   public boolean isInWork() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CurrentStateType).equals(StateType.Working.name());
   }

   @Override
   public String getCompletedFromState() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CompletedFromState);
   }

   @Override
   public String getCancelledFromState() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CancelledFromState);
   }

   @Override
   public void setCompletedFromState(String string) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public void setCancelledFromState(String string) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public void setStateType(StateType StateType) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public void setCompletedDate(Date completedDate) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public void setCancelledDate(Date cancelledDate) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public void setCompletedBy(IAtsUser completedBy) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public void setCancelledBy(IAtsUser cancelledBy) throws OseeCoreException {
      throw new OseeStateException("Not implemented yet");
   }

   @Override
   public String getArtifactTypeName() throws OseeCoreException {
      return artifact.getArtifactType().getName();
   }

   @Override
   public IAtsUser getCreatedBy() throws OseeCoreException {
      return atsUserService.getUserById(artifact.getSoleAttributeValue(AtsAttributeTypes.CreatedBy, ""));
   }

   @Override
   public Date getCreatedDate() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.CreatedDate);
   }

}
