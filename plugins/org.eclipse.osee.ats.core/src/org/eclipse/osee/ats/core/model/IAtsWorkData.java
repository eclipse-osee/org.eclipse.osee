/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.Date;
import org.eclipse.osee.ats.workdef.api.StateType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

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

}
