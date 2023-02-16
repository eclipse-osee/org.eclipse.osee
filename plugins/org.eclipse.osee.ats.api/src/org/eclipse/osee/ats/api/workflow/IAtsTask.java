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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTask extends IAtsWorkItem {

   public static boolean isOfType(Object object) {
      return object instanceof IAtsTask;
   }

   IAtsTask SENTINEL = createSentinel();

   public static IAtsTask createSentinel() {
      final class IAtsTaskSentinel extends NamedIdBase implements IAtsTask {

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
         public String getCurrentStateName() {
            return "";
         }

         @Override
         public StateType getCurrentStateType() {
            return null;
         }

      }
      return new IAtsTaskSentinel();
   }

}
