/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.query.IAtsQuery;
import org.eclipse.osee.ats.api.query.IAtsWorkItemFilter;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsQueryImpl implements IAtsQuery {

   protected final List<AtsAttributeQuery> andAttr = new ArrayList<>();
   protected final HashMap<IRelationTypeSide, IAtsObject> andRels = new HashMap<>();
   protected IAtsTeamDefinition teamDef;
   protected StateType[] stateType;
   protected Class<? extends IAtsWorkItem> clazz;
   protected IArtifactType[] artifactTypes;
   protected Long[] uuids;
   private final IAtsServices services;

   public AbstractAtsQueryImpl(IAtsServices services) {
      this.services = services;
   }

   @Override
   public IAtsQuery isOfType(IArtifactType... artifactTypes) {
      this.artifactTypes = artifactTypes;
      return this;
   }

   @Override
   public IAtsQuery isOfType(Class<? extends IAtsWorkItem> clazz) throws OseeCoreException {
      this.clazz = clazz;
      return this;
   }

   @Override
   public IAtsQuery fromTeam(IAtsTeamDefinition teamDef) throws OseeCoreException {
      this.teamDef = teamDef;
      return this;
   }

   @Override
   public IAtsQuery isStateType(StateType... stateType) throws OseeCoreException {
      this.stateType = stateType;
      return this;
   }

   @Override
   public IAtsQuery andAttr(IAttributeType attributeType, Collection<String> values, QueryOption... queryOptions) throws OseeCoreException {
      andAttr.add(new AtsAttributeQuery(attributeType, values, queryOptions));
      return this;
   }

   @Override
   public IAtsQuery andRelated(IAtsObject object, IRelationTypeSide relation) {
      andRels.put(relation, object);
      return this;
   }

   @Override
   public IAtsQuery andUuids(Long... uuids) {
      this.uuids = uuids;
      return this;
   }

   @Override
   public IAtsQuery andAtsIds(Collection<String> atsIds) {
      return andAttr(AtsAttributeTypes.AtsId, atsIds);
   }

   @Override
   public IAtsQuery andLegacyIds(Collection<String> legacyIds) {
      return andAttr(AtsAttributeTypes.LegacyPcrId, legacyIds);
   }

   @Override
   public IAtsWorkItemFilter andFilter() throws OseeCoreException {
      return new AtsWorkItemFilter(getItems(), services);
   }

   protected List<IArtifactType> getArtifactTypes() {
      List<IArtifactType> artifactTypes = new LinkedList<>();
      if (IAtsTeamWorkflow.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.TeamWorkflow);
      } else if (IAtsAction.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.Action);
      } else if (IAtsTask.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.Task);
      } else if (IAtsPeerToPeerReview.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.PeerToPeerReview);
      } else if (IAtsDecisionReview.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.DecisionReview);
      } else if (IAtsAbstractReview.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.ReviewArtifact);
      } else if (IAtsGoal.class.isAssignableFrom(clazz)) {
         artifactTypes.add(AtsArtifactTypes.Goal);
      } else {
         artifactTypes.add(AtsArtifactTypes.AbstractWorkflowArtifact);
      }
      return artifactTypes;
   }

   @Override
   public IAtsQuery andAttr(IAttributeType attributeType, String value, QueryOption... queryOption) {
      return andAttr(attributeType, Collections.singleton(value), queryOption);
   }

   @Override
   public <T extends IAtsWorkItem> ResultSet<T> getResults() {
      return ResultSets.newResultSet(getItems());
   }

   @Override
   public IAtsQuery isGoal() {
      return isOfType(AtsArtifactTypes.Goal);
   }

   @Override
   public IAtsQuery andAssignee(IAtsUser... assignees) {
      List<String> userIds = new ArrayList<>();
      for (IAtsUser user : assignees) {
         userIds.add("<" + user.getUserId() + ">");
      }
      return andAttr(AtsAttributeTypes.CurrentState, userIds, QueryOption.CONTAINS_MATCH_OPTIONS);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends ArtifactId> ResultSet<T> getResultArtifacts() {
      List<T> items = new ArrayList<>();
      for (IAtsWorkItem workItem : getResults()) {
         items.add((T) workItem.getStoreObject());
      }
      return ResultSets.newResultSet(items);
   }

}
