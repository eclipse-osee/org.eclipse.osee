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
package org.eclipse.osee.ats.api.workflow;

import java.util.Date;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkData {

   public boolean isCompleted() throws OseeCoreException;

   public IAtsUser getCompletedBy() throws OseeCoreException;

   public Date getCompletedDate() throws OseeCoreException;

   public boolean isCancelled() throws OseeCoreException;

   public IAtsUser getCancelledBy() throws OseeCoreException;

   public Date getCancelledDate() throws OseeCoreException;

   public boolean isCompletedOrCancelled() throws OseeCoreException;

   public boolean isInWork() throws OseeCoreException;

   public String getCompletedFromState() throws OseeCoreException;

   public String getCancelledFromState() throws OseeCoreException;

   public void setCompletedFromState(String string) throws OseeCoreException;

   public void setCancelledFromState(String string) throws OseeCoreException;

   public void setStateType(StateType StateType) throws OseeCoreException;

   public void setCompletedDate(Date completedDate) throws OseeCoreException;

   public void setCancelledDate(Date cancelledDate) throws OseeCoreException;

   public void setCompletedBy(IAtsUser completedBy) throws OseeCoreException;

   public void setCancelledBy(IAtsUser cancelledBy) throws OseeCoreException;

   public String getArtifactTypeName() throws OseeCoreException;

   public IAtsUser getCreatedBy() throws OseeCoreException;

   public Date getCreatedDate() throws OseeCoreException;

}
