/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.HasAssignees;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItem extends IAtsObject, HasAssignees {

   String getAtsId();

   IAtsTeamWorkflow getParentTeamWorkflow();

   IAtsStateManager getStateMgr();

   IAtsLog getLog();

   WorkDefinition getWorkDefinition();

   StateDefinition getStateDefinition();

   AtsUser getCreatedBy();

   Date getCreatedDate();

   AtsUser getCompletedBy();

   AtsUser getCancelledBy();

   String getCompletedFromState();

   String getCancelledFromState();

   String getArtifactTypeName();

   Date getCompletedDate();

   Date getCancelledDate();

   String getCancelledReason();

   IAtsAction getParentAction();

   IAtsWorkItem SENTINEL = createSentinel();

   default boolean isTeamWorkflow() {
      return isOfType(AtsArtifactTypes.TeamWorkflow);
   }

   default boolean isDecisionReview() {
      return isOfType(AtsArtifactTypes.DecisionReview);
   }

   default boolean isPeerReview() {
      return isOfType(AtsArtifactTypes.PeerToPeerReview);
   }

   default boolean isTask() {
      return isOfType(AtsArtifactTypes.Task);
   }

   default boolean isReview() {
      return isOfType(AtsArtifactTypes.AbstractReview);
   }

   default boolean isSprint() {
      return isOfType(AtsArtifactTypes.AgileSprint);
   }

   default boolean isBacklog() {
      return isOfType(AtsArtifactTypes.AgileBacklog);
   }

   default boolean isGoal() {
      return this instanceof IAtsGoal;
   }

   default boolean isInWork() {
      return getStateDefinition().isWorking();
   }

   default boolean isCompleted() {
      return getStateDefinition().isCompleted();
   }

   default boolean isCancelled() {
      return getStateDefinition().isCancelled();
   }

   default boolean isCompletedOrCancelled() {
      return isCompleted() || isCancelled();
   }

   default boolean hasAction() {
      return true;
   }

   void setStateMgr(IAtsStateManager stateMgr);

   void clearCaches();

   boolean isInState(IStateToken state);

   @Override
   default public Collection<String> getTags() {
      return getAtsApi().getAttributeResolver().getAttributesToStringList(getStoreObject(),
         CoreAttributeTypes.StaticId);
   }

   default public void setTags(List<String> tags) {
      throw new UnsupportedOperationException("Invalid method for IAtsWorkItem; use IAtsChangeSet");
   }

   @Override
   default public boolean hasTag(String tag) {
      return getTags().contains(tag);
   }

   @Override
   default String toStringWithId() {
      String atsId = "";
      try {
         atsId = getAtsId();
      } catch (Exception ex) {
         atsId = "Exception: " + ex.getLocalizedMessage();
      }
      return String.format("[%s]-[%s]-[%s]", getName(), atsId, getIdString());
   }

   @Override
   default String toStringWithId(int nameTruncateLength) {
      return String.format("[%s]-[%s]-[%s]", Strings.truncate(getName(), nameTruncateLength, true), getAtsId(),
         getIdString());
   }

   default String toStringWithAtsId() {
      return String.format("[%s]-[%s]", getName(), getAtsId());
   }

   default boolean isChangeRequest() {
      return getArtifactType().inheritsFrom(AtsArtifactTypes.AbstractChangeRequestWorkflow);
   }

   public String getCurrentStateName();

   StateType getCurrentStateType();

   public static IAtsWorkItem createSentinel() {
      final class IAtsWorkItemSentinel extends NamedIdBase implements IAtsWorkItem {

         @Override
         public ArtifactTypeToken getArtifactType() {
            return null;
         }

         @Override
         public List<AtsUser> getAssignees() {
            return null;
         }

         @Override
         public List<AtsUser> getImplementers() {
            return null;
         }

         @Override
         public String getAtsId() {
            return null;
         }

         @Override
         public IAtsTeamWorkflow getParentTeamWorkflow() {
            return null;
         }

         @Override
         public IAtsStateManager getStateMgr() {
            return null;
         }

         @Override
         public IAtsLog getLog() {
            return null;
         }

         @Override
         public WorkDefinition getWorkDefinition() {
            return null;
         }

         @Override
         public StateDefinition getStateDefinition() {
            return null;
         }

         @Override
         public AtsUser getCreatedBy() {
            return null;
         }

         @Override
         public Date getCreatedDate() {
            return null;
         }

         @Override
         public AtsUser getCompletedBy() {
            return null;
         }

         @Override
         public AtsUser getCancelledBy() {
            return null;
         }

         @Override
         public String getCompletedFromState() {
            return null;
         }

         @Override
         public String getCancelledFromState() {
            return null;
         }

         @Override
         public String getArtifactTypeName() {
            return null;
         }

         @Override
         public Date getCompletedDate() {
            return null;
         }

         @Override
         public Date getCancelledDate() {
            return null;
         }

         @Override
         public String getCancelledReason() {
            return null;
         }

         @Override
         public IAtsAction getParentAction() {
            return null;
         }

         @Override
         public void setStateMgr(IAtsStateManager stateMgr) {
            // do nothing
         }

         @Override
         public void clearCaches() {
            // do nothing
         }

         @Override
         public AtsApi getAtsApi() {
            return null;
         }

         @Override
         public boolean isInState(IStateToken state) {
            return false;
         }

         @Override
         public Collection<WorkType> getWorkTypes() {
            return null;
         }

         @Override
         public boolean isWorkType(WorkType workType) {
            return false;
         }

         @Override
         public Collection<String> getTags() {
            return null;
         }

         @Override
         public boolean hasTag(String tag) {
            return false;
         }

         @Override
         public boolean isSprint() {
            return false;
         }

         @Override
         public String getCurrentStateName() {
            return "";
         }

         @Override
         public StateType getCurrentStateType() {
            return null;
         }

      }
      return new IAtsWorkItemSentinel();
   }

}