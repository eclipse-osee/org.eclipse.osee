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
package org.eclipse.osee.ats.impl.internal.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.query.AbstractAtsQueryImpl;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsQueryImpl extends AbstractAtsQueryImpl {

   private final IAtsServer atsServer;

   public AtsQueryImpl(IAtsServer atsServer) {
      super(atsServer.getWorkItemService());
      this.atsServer = atsServer;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends IAtsWorkItem> Collection<T> getItems() throws OseeCoreException {
      QueryBuilder query = atsServer.getOrcsApi().getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch());

      // WorkItem type
      if (clazz != null) {
         List<IArtifactType> artifactTypes = getArtifactTypes();
         query.andIsOfType(artifactTypes.toArray(new IArtifactType[artifactTypes.size()]));
      }

      // team
      if (teamDef != null) {
         query.and(AtsAttributeTypes.TeamDefinition, Collections.singleton(teamDef.getGuid()));
      }

      // state
      if (stateType != null) {
         List<String> stateTypes = new ArrayList<String>();
         for (StateType type : stateType) {
            stateTypes.add(type.name());
         }
         query.and(AtsAttributeTypes.CurrentStateType, stateTypes);
      }

      // attributes
      if (!andAttr.isEmpty()) {
         for (Entry<IAttributeType, Collection<String>> entry : andAttr.entrySet()) {
            query.and(entry.getKey(), entry.getValue(), getQueryOptions(entry.getKey()));
         }
      }

      if (!andRels.isEmpty()) {
         for (Entry<IRelationTypeSide, IAtsObject> entry : andRels.entrySet()) {
            query.andRelatedTo(entry.getKey(), (ArtifactReadable) entry.getValue().getStoreObject());
         }
      }

      Set<T> workItems = new HashSet<T>();
      Iterator<ArtifactReadable> iterator = query.getResults().iterator();
      while (iterator.hasNext()) {
         workItems.add((T) atsServer.getWorkItemFactory().getWorkItem(iterator.next()));
      }
      return workItems;

   }
}
